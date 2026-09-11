package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.PaymentService
import com.sweetshop.backend.util.SecurityUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/create-order")
    fun createPaymentOrder(
        @Valid @RequestBody request: CreatePaymentOrderRequest
    ): ResponseEntity<ApiResponse<PaymentOrderResponse>> {
        val userId = SecurityUtils.getCurrentUserId()
        val response = paymentService.createPaymentOrder(userId, request)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }

    @PostMapping("/verify")
    fun verifyPayment(
        @Valid @RequestBody request: VerifyPaymentRequest
    ): ResponseEntity<ApiResponse<PaymentVerificationResponse>> {
        val userId = SecurityUtils.getCurrentUserId()
        val response = paymentService.verifyPayment(userId, request)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }
}
