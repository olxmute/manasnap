package com.manasnap.client

import com.manasnap.config.ScryfallProperties
import com.manasnap.dto.scryfall.ScryfallCardResponse
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class ScryfallClient(
    private val webClient: WebClient,
    private val scryfallProperties: ScryfallProperties,
    private val scryfallRateLimiter: RateLimiter,
) {

    suspend fun getCardByName(cardName: String): ScryfallCardResponse {
        return webClient.get()
            .uri {
                it.path(scryfallProperties.endpoints.cardByName)
                    .queryParam("exact", cardName)
                    .build()
            }
            .retrieve()
            .bodyToMono<ScryfallCardResponse>()
            .transformDeferred(RateLimiterOperator.of(scryfallRateLimiter))
            .awaitSingle()
    }
}
