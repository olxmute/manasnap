package com.manasnap.controller

import com.manasnap.dto.CardsRequest
import com.manasnap.dto.OperationCreatedResponse
import com.manasnap.dto.OperationResponse
import com.manasnap.service.CardOperationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/cards")
class CardController(
    private val cardOperationService: CardOperationService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun submitCards(@RequestBody @Valid request: CardsRequest): OperationCreatedResponse =
        cardOperationService.submitCards(request)

    @GetMapping("/{operationId}")
    suspend fun getOperationStatus(@PathVariable operationId: String): OperationResponse =
        cardOperationService.getOperationStatus(operationId)
}
