package com.manasnap.config

import kotlinx.coroutines.sync.Semaphore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun scryfallWebClient(scryfallProperties: ScryfallProperties): WebClient = WebClient.builder()
        .baseUrl(scryfallProperties.baseUrl)
        .build()

    @Bean
    fun scryfallSemaphore(scryfallProperties: ScryfallProperties) =
        Semaphore(scryfallProperties.maxConcurrentRequests)
}
