package com.manasnap.service

import com.manasnap.dto.CardFailureResponse
import com.manasnap.dto.CardResultResponse
import com.manasnap.dto.CardsRequest
import com.manasnap.dto.OperationResponse
import com.manasnap.entity.Operation
import com.manasnap.entity.OperationStatus
import com.manasnap.repository.OperationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CardOperationService(
    private val operationRepository: OperationRepository,
    private val cardProcessingService: CardProcessingService,
    private val backgroundScope: CoroutineScope
) {

    /**
     * Receives the cards request, creates an operation record,
     * then triggers asynchronous processing while returning an initial response.
     */
    suspend fun submitCards(request: CardsRequest): OperationResponse = withContext(Dispatchers.IO) {
        val operation = Operation()
        operationRepository.save(operation)

        // Trigger background processing (non-blocking)
        backgroundScope.launch {
            cardProcessingService.processCards(operation, request.cardNames)
        }

        // Return a minimal response indicating the operation has been scheduled.
        OperationResponse(operationId = operation.id, status = operation.status)
    }

    /**
     * Retrieves the operation record and maps the entity to a response DTO.
     */
    @Transactional
    suspend fun getOperationStatus(operationId: String): OperationResponse = withContext(Dispatchers.IO) {
        val operation: Operation = operationRepository.findById(operationId)
            .orElseThrow { RuntimeException("Operation not found") }
        val response = OperationResponse(
            operationId = operation.id,
            status = operation.status
        )
        if (operation.status != OperationStatus.PROCESSING) {
            val results = operation.cardResults
            if (results.isNotEmpty()) {
                val successResults = results.filter { it.error == null }
                    .map { CardResultResponse(it.cardName, it.pngUrl) }
                val failures = results.filter { it.error != null }
                    .map { CardFailureResponse(it.cardName, it.error!!) }
                response.results = successResults
                response.failures = failures
                if (successResults.isEmpty() && failures.isNotEmpty()) {
                    response.error = "All requests failed"
                }
            }
        }
        response
    }
}