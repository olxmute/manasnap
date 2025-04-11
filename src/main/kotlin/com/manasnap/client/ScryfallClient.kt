package com.manasnap.client

import com.manasnap.config.ScryfallProperties
import com.manasnap.dto.scryfall.ScryfallCardResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class ScryfallClient(
    private val webClient: WebClient,
    private val semaphore: Semaphore,
    private val scryfallProperties: ScryfallProperties
) {

    suspend fun getCardByName(cardName: String): ScryfallCardResponse {
        val encodedName = URLEncoder.encode(cardName, StandardCharsets.UTF_8)
        return semaphore.withPermit {
            withTimeout(scryfallProperties.requestTimeout) {
                webClient.get()
                    .uri {
                        it.path(scryfallProperties.endpoints.cardByName)
                            .queryParam("exact", encodedName)
                            .build()
                    }
                    .retrieve()
                    .awaitBody()
            }
        }
    }
}
