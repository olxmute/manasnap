package com.manasnap.dto

import com.manasnap.entity.OperationStatus

data class OperationResponse(
    val operationId: String,
    var status: OperationStatus? = null,
    var results: List<CardResultResponse>? = null,
    var failures: List<CardFailureResponse>? = null,
    var error: String? = null,
)

data class CardResultResponse(
    val cardName: String,
    val pngUrl: String?
)

data class CardFailureResponse(
    val cardName: String,
    val error: String
)