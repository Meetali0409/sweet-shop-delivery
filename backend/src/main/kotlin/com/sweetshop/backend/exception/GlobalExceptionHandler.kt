package com.sweetshop.backend.exception

import com.sweetshop.backend.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Resource not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                code = "NOT_FOUND",
                message = ex.message ?: "Resource not found",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ErrorResponse> {
        logger.warn("Bad request: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = "BAD_REQUEST",
                message = ex.message ?: "Bad request",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        logger.warn("Unauthorized: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                code = "UNAUTHORIZED",
                message = ex.message ?: "Unauthorized",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException): ResponseEntity<ErrorResponse> {
        logger.warn("Forbidden: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                code = "FORBIDDEN",
                message = ex.message ?: "Forbidden",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ErrorResponse> {
        logger.warn("Conflict: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(
                code = "CONFLICT",
                message = ex.message ?: "Conflict",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(OutOfStockException::class)
    fun handleOutOfStock(ex: OutOfStockException): ResponseEntity<ErrorResponse> {
        logger.warn("Out of stock: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = "OUT_OF_STOCK",
                message = ex.message ?: "Product out of stock",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(InvalidCouponException::class)
    fun handleInvalidCoupon(ex: InvalidCouponException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid coupon: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = "INVALID_COUPON",
                message = ex.message ?: "Invalid coupon",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ErrorResponse> {
        logger.warn("Bad credentials: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                code = "INVALID_CREDENTIALS",
                message = "Invalid email or password",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        logger.warn("Access denied: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                code = "ACCESS_DENIED",
                message = "You don't have permission to access this resource",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }
        logger.warn("Validation error: {}", errors)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = "VALIDATION_ERROR",
                message = errors,
                timestamp = LocalDateTime.now().toString()
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                code = "INTERNAL_ERROR",
                message = "An unexpected error occurred. Please try again later.",
                timestamp = LocalDateTime.now().toString()
            )
        )
    }
}
