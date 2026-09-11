package com.sweetshop.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreatePaymentOrderRequest(
    @field:NotNull(message = "Order ID is required")
    val orderId: Long
)

data class PaymentOrderResponse(
    val razorpayOrderId: String,
    val amount: Long,
    val currency: String,
    val orderId: Long,
    val keyId: String
)

data class VerifyPaymentRequest(
    @field:NotNull(message = "Order ID is required")
    val orderId: Long,

    @field:NotBlank(message = "Razorpay order ID is required")
    val razorpayOrderId: String,

    @field:NotBlank(message = "Razorpay payment ID is required")
    val razorpayPaymentId: String,

    @field:NotBlank(message = "Razorpay signature is required")
    val razorpaySignature: String
)

data class PaymentVerificationResponse(
    val success: Boolean,
    val orderId: Long,
    val paymentId: String?,
    val message: String
)
