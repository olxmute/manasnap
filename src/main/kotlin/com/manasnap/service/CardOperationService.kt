package com.manasnap.service

import com.manasnap.config.coroutines.CoroutineDispatcherProvider
import com.manasnap.dto.CardFailureResponse
import com.manasnap.dto.CardResultResponse
import com.manasnap.dto.CardsRequest
import com.manasnap.dto.OperationResponse
import com.manasnap.entity.Operation
import com.manasnap.entity.OperationStatus
import com.manasnap.exception.OperationNotFoundException
import com.manasnap.repository.OperationRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CardOperationService(
    private val operationRepository: OperationRepository,
    private val cardProcessingService: CardProcessingService,
    private val backgroundScope: CoroutineScope,
    private val dispatcherProvider: CoroutineDispatcherProvider
) {

    private val logger = LoggerFactory.getLogger(CardOperationService::class.java)

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logger.error("Unhandled exception in background card processing: {}", throwable.message, throwable)
    }

    /**
     * Receives the cards request, creates an operation record,
     * then triggers asynchronous processing while returning an initial response.
     */
    suspend fun submitCards(request: CardsRequest): OperationResponse = withContext(dispatcherProvider.io) {
        val operation = Operation()
        operationRepository.save(operation)

        backgroundScope.launch(exceptionHandler) {
            cardProcessingService.processCards(operation, request.cardNames)
        }

        OperationResponse(operationId = operation.id!!)
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
                results = successes
                failures = failuresList
                if (successes.isEmpty() && failuresList.isNotEmpty()) {
                    error = "All requests failed"
                }
            }
        }
    }
}
