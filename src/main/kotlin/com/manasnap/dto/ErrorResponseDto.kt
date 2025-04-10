package com.manasnap.dto

data class ErrorResponseDto(
    val error: String,
    val message: String,
    val status: Int
)
