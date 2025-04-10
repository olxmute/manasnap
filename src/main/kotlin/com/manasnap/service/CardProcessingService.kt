package com.manasnap.service

import com.manasnap.dto.scryfall.ScryfallCardResponse
import com.manasnap.entity.CardResult
import com.manasnap.entity.Operation
import com.manasnap.entity.OperationStatus
import com.manasnap.repository.CardResultRepository
import com.manasnap.repository.OperationRepository
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class CardProcessingService(
    private val operationRepository: OperationRepository,
    private val cardResultRepository: CardResultRepository
) {
    private val logger = LoggerFactory.getLogger(CardProcessingService::class.java)
    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://api.scryfall.com")
        .build()

    // Runs all card fetch operations concurrently.
    suspend fun processCards(operation: Operation, cardNames: List<String>) = coroutineScope {
        val deferredResults = cardNames.map { cardName ->
            async { processSingleCard(operation, cardName) }
        }

        val results = deferredResults.awaitAll()

        val anySuccess = results.any { it.error == null }
        val allSuccess = results.all { it.error == null }

        operation.status = when {
            allSuccess -> OperationStatus.SUCCESS
            !anySuccess -> OperationStatus.FAILURE
            else -> OperationStatus.PARTIAL_SUCCESS
        }
        operationRepository.save(operation)
    }

    // For each card, fetches the Scryfall card data and extracts the PNG URL.
    private suspend fun processSingleCard(operation: Operation, cardName: String): CardResult {
        val cardResult = CardResult(cardName = cardName, operation = operation)
        try {
            val encodedName = URLEncoder.encode(cardName, StandardCharsets.UTF_8)
            val cardResponse: ScryfallCardResponse = webClient.get()
                .uri("/cards/named?exact=$encodedName")
                .retrieve()
                .bodyToMono<ScryfallCardResponse>()
                .awaitSingle()
            val pngUrl = cardResponse.imageUris?.png
            if (pngUrl.isNullOrBlank()) {
                throw RuntimeException("PNG URL not found for card: $cardName")
            }
            cardResult.pngUrl = pngUrl
            logger.info("Fetched PNG URL for [{}]: {}", cardName, pngUrl)
        } catch (ex: WebClientResponseException) {
            if (ex.statusCode == HttpStatus.NOT_FOUND) {
                cardResult.error = "Card not found"
                logger.warn("Card not found: {}", cardName)
            } else {
                cardResult.error = "Error fetching card: ${ex.message}"
                logger.error("Error fetching card [{}]: {}", cardName, ex.message)
            }
        } catch (e: Exception) {
            cardResult.error = "Exception: ${e.message}"
            logger.error("Exception processing card [{}]: {}", cardName, e.message)
        }
        cardResultRepository.save(cardResult)
        return cardResult
    }
}