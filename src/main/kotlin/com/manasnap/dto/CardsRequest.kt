package com.manasnap.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CardsRequest(
    @field:NotEmpty
    @field:Valid
    val cardNames: List<@NotBlank String>
)
