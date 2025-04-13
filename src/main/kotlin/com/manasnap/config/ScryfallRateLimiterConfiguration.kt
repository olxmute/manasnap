package com.manasnap.config

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ScryfallRateLimiterConfiguration {
    @Bean
    fun scryfallRateLimiter(rateLimiterRegistry: RateLimiterRegistry): RateLimiter =
        rateLimiterRegistry.rateLimiter("scryfallRateLimiter")
}
