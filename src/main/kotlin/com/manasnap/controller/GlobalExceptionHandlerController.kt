package com.manasnap.controller

import com.manasnap.dto.ErrorResponseDto
import com.manasnap.exception.CardProcessingException
import com.manasnap.exception.OperationNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class GlobalExceptionHandlerController {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandlerController::class.java)

    @ExceptionHandler(OperationNotFoundException::class)
    fun handleOperationNotFound(ex: OperationNotFoundException): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            error = "NotFound",
            message = ex.message ?: "Operation not found",
            status = HttpStatus.NOT_FOUND.value()
        )
        logger.error("Operation not found: {}", ex.message, ex)
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(CardProcessingException::class)
    fun handleCardProcessingException(ex: CardProcessingException): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            error = "CardProcessingError",
            message = ex.message ?: "Failed to process card",
            status = HttpStatus.BAD_REQUEST.value()
        )
        logger.error("Card processing error: {}", ex.message, ex)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // TODO: add more details about invalid input (exact fields and errors)
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(ex: WebExchangeBindException): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            error = "InvalidInput",
            message = "Invalid input.",
            status = HttpStatus.UNPROCESSABLE_ENTITY.value()
        )
        logger.error("Invalid input: {}", ex.message, ex)
        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            error = "InternalServerError",
            message = "An unexpected error occurred.",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        logger.error("Unhandled exception occurred: {}", ex.message, ex)
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
