package com.manasnap.service

import com.manasnap.client.ScryfallClient
import com.manasnap.config.coroutines.CoroutineDispatcherProvider
import com.manasnap.entity.CardResult
import com.manasnap.entity.Operation
import com.manasnap.entity.OperationStatus
import com.manasnap.exception.CardProcessingException
import com.manasnap.repository.CardResultRepository
import com.manasnap.repository.OperationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CardProcessingService(
    private val operationRepository: OperationRepository,
    private val cardResultRepository: CardResultRepository,
    private val scryfallClient: ScryfallClient,
    private val dispatcherProvider: CoroutineDispatcherProvider
) {
    private val logger = LoggerFactory.getLogger(CardProcessingService::class.java)

    suspend fun processCards(operation: Operation, cardNames: List<String>) = coroutineScope {
        logger.info("Starting to process {} cards for operation {}", cardNames.size, operation.id)

        val deferredResults = cardNames.map { cardName ->
            async { processSingleCard(operation, cardName) }
        }

        val results = deferredResults.awaitAll()
        updateOperationStatus(operation, results)

        logger.info("Completed processing cards for operation {} with status {}", operation.id, operation.status)
    }

    private fun updateOperationStatus(operation: Operation, results: List<CardResult>) {
        val anySuccess = results.any { it.error == null }
        val allSuccess = results.all { it.error == null }

        operation.status = when {
            allSuccess -> OperationStatus.SUCCESS
            !anySuccess -> OperationStatus.FAILURE
            else -> OperationStatus.PARTIAL_SUCCESS
        }
        operationRepository.save(operation)
    }

    private suspend fun processSingleCard(operation: Operation, cardName: String): CardResult {
        val cardResult = CardResult().apply {
            this.cardName = cardName
            this.operation = operation
        }
        try {
            val cardResponse = scryfallClient.getCardByName(cardName)
            val pngUrl = cardResponse.imageUris?.png

            if (pngUrl.isNullOrBlank()) {
                throw CardProcessingException("PNG URL not found for card: $cardName")
            }

            cardResult.pngUrl = pngUrl
            logger.info("Successfully fetched PNG URL for card [{}]: {}", cardName, pngUrl)
        } catch (ex: Exception) {
            cardResult.error = "Exception: ${ex.message}"
            logger.error("Exception processing card [{}]: {}", cardName, ex.message, ex)
        }

        withContext(dispatcherProvider.io) {
            cardResultRepository.save(cardResult)
        }
        return cardResult
    }

}
