package com.manasnap.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "scryfall.api")
data class ScryfallProperties(
    val baseUrl: String,
    val endpoints: Endpoints
) {
    data class Endpoints(
        val cardByName: String
    )
}
