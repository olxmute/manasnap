package com.manasnap.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CardsRequest(
    @field:Size(min = 1, max = 100)
    @field:Valid
    val cardNames: List<@NotBlank String>
)
