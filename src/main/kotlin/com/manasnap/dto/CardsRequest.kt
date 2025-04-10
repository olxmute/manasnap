package com.manasnap.dto

import jakarta.validation.constraints.NotEmpty

data class CardsRequest(
    @get:NotEmpty val cardNames: List<String>
)
