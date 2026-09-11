package com.sweetshop.customer.data.dto

data class CreatePaymentOrderRequest(
    val orderId: Long
)

data class PaymentOrderResponse(
    val razorpayOrderId: String = "",
    val amount: Long = 0,
    val currency: String = "INR",
    val orderId: Long = 0,
    val keyId: String = ""
)

data class VerifyPaymentRequest(
    val orderId: Long,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String
)

data class PaymentVerificationResponse(
    val success: Boolean = false,
    val orderId: Long = 0,
    val paymentId: String? = null,
    val message: String = ""
)
