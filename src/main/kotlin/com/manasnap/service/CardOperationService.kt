package com.manasnap.service

import com.manasnap.config.coroutines.CoroutineDispatcherProvider
import com.manasnap.dto.CardFailureResponse
import com.manasnap.dto.CardResultResponse
import com.manasnap.dto.CardsRequest
import com.manasnap.dto.OperationCreatedResponse
import com.manasnap.dto.OperationResponse
import com.manasnap.entity.Operation
import com.manasnap.entity.OperationStatus
import com.manasnap.exception.OperationNotFoundException
import com.manasnap.repository.OperationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class CardOperationService(
    private val operationRepository: OperationRepository,
    private val cardProcessingService: CardProcessingService,
    private val backgroundScope: CoroutineScope,
    private val dispatcherProvider: CoroutineDispatcherProvider
) {

    /**
     * Receives the cards request, creates an operation record,
     * then triggers asynchronous processing while returning an initial response.
     */
    suspend fun submitCards(request: CardsRequest): OperationCreatedResponse = withContext(dispatcherProvider.io) {
        val operation = Operation()
        operationRepository.save(operation)

        backgroundScope.launch {
            cardProcessingService.processCards(operation, request.cardNames)
        }

        OperationCreatedResponse(operationId = operation.id!!)
    }

    /**
     * Retrieves the operation record and maps the entity to a response DTO.
     */
    suspend fun getOperationStatus(operationId: String): OperationResponse {
        val operation = withContext(dispatcherProvider.io) {
            operationRepository.findByIdWithCardResult(operationId)
                ?: throw OperationNotFoundException("Operation not found")
        }
        return OperationResponse(operationId = operation.id!!, status = operation.status).apply {
            if (operation.status != OperationStatus.PROCESSING) {
                val successes = operation.cardResults.filter { it.error == null }
                    .map { CardResultResponse(it.cardName, it.pngUrl) }
                val failuresList = operation.cardResults.filter { it.error != null }
                    .map { CardFailureResponse(it.cardName, it.error!!) }

                if (successes.isEmpty() && failuresList.isNotEmpty()) {
                    error = "All requests failed"
                } else {
                    results = successes.ifEmpty { null }
                    failures = failuresList.ifEmpty { null }
                }
            }
        }
    }
}
