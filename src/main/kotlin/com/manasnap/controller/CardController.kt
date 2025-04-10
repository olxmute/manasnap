package com.manasnap.controller

import com.manasnap.dto.CardsRequest
import com.manasnap.dto.OperationResponse
import com.manasnap.service.CardOperationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cards")
class CardController(
    private val cardOperationService: CardOperationService
) {

    @PostMapping
    suspend fun submitCards(@RequestBody request: CardsRequest): OperationResponse =
        cardOperationService.submitCards(request)

    @GetMapping("/{operationId}")
    suspend fun getOperationStatus(@PathVariable operationId: String): OperationResponse =
        cardOperationService.getOperationStatus(operationId)
}