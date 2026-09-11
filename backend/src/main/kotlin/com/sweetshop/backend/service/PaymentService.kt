package com.sweetshop.backend.service

import com.razorpay.Order as RazorpayOrder
import com.razorpay.RazorpayClient
import com.razorpay.Utils
import com.sweetshop.backend.dto.CreatePaymentOrderRequest
import com.sweetshop.backend.dto.PaymentOrderResponse
import com.sweetshop.backend.dto.PaymentVerificationResponse
import com.sweetshop.backend.dto.VerifyPaymentRequest
import com.sweetshop.backend.entity.PaymentStatus
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.repository.OrderRepository
import com.sweetshop.backend.repository.ShopConfigRepository
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    @Value("\${app.razorpay.key-id:}")
    private val razorpayKeyId: String,

    @Value("\${app.razorpay.key-secret:}")
    private val razorpayKeySecret: String,

    private val orderRepository: OrderRepository,
    private val shopConfigRepository: ShopConfigRepository
) {
    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    private val razorpayClient: RazorpayClient? by lazy {
        if (razorpayKeyId.isNotBlank() && razorpayKeySecret.isNotBlank()) {
            RazorpayClient(razorpayKeyId, razorpayKeySecret)
        } else {
            logger.warn("Razorpay credentials not configured. Online payments disabled.")
            null
        }
    }

    fun isPaymentEnabled(): Boolean = razorpayClient != null

    @Transactional
    fun createPaymentOrder(userId: Long, request: CreatePaymentOrderRequest): PaymentOrderResponse {
        val client = razorpayClient
            ?: throw BadRequestException("Online payment is not configured")

        val order = orderRepository.findById(request.orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: ${request.orderId}") }

        if (order.user.id != userId) {
            throw ResourceNotFoundException("Order not found with id: ${request.orderId}")
        }

        if (order.paymentStatus == PaymentStatus.PAID) {
            throw BadRequestException("Order is already paid")
        }

        val config = shopConfigRepository.findAll().firstOrNull()
        val currency = config?.currencyCode ?: "INR"

        val amountInPaise = order.totalAmount.multiply(java.math.BigDecimal(100)).toLong()

        val orderRequest = JSONObject()
        orderRequest.put("amount", amountInPaise)
        orderRequest.put("currency", currency)
        orderRequest.put("receipt", order.orderNumber)
        orderRequest.put("notes", JSONObject().put("orderId", order.id))

        val razorpayOrder: RazorpayOrder = client.orders.create(orderRequest)
        val razorpayOrderId = razorpayOrder.get<String>("id")

        order.razorpayOrderId = razorpayOrderId
        orderRepository.save(order)

        logger.info("Razorpay order created: {} for order: {}", razorpayOrderId, order.orderNumber)

        return PaymentOrderResponse(
            razorpayOrderId = razorpayOrderId,
            amount = amountInPaise,
            currency = currency,
            orderId = order.id,
            keyId = razorpayKeyId
        )
    }

    @Transactional
    fun verifyPayment(userId: Long, request: VerifyPaymentRequest): PaymentVerificationResponse {
        val order = orderRepository.findById(request.orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: ${request.orderId}") }

        if (order.user.id != userId) {
            throw ResourceNotFoundException("Order not found with id: ${request.orderId}")
        }

        if (order.razorpayOrderId != request.razorpayOrderId) {
            throw BadRequestException("Razorpay order ID mismatch")
        }

        try {
            val attributes = JSONObject()
            attributes.put("razorpay_order_id", request.razorpayOrderId)
            attributes.put("razorpay_payment_id", request.razorpayPaymentId)
            attributes.put("razorpay_signature", request.razorpaySignature)

            val isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret)

            if (isValid) {
                order.razorpayPaymentId = request.razorpayPaymentId
                order.razorpaySignature = request.razorpaySignature
                order.paymentStatus = PaymentStatus.PAID
                orderRepository.save(order)

                logger.info("Payment verified for order: {}", order.orderNumber)

                return PaymentVerificationResponse(
                    success = true,
                    orderId = order.id,
                    paymentId = request.razorpayPaymentId,
                    message = "Payment verified successfully"
                )
            } else {
                order.paymentStatus = PaymentStatus.FAILED
                orderRepository.save(order)

                logger.warn("Payment signature verification failed for order: {}", order.orderNumber)

                return PaymentVerificationResponse(
                    success = false,
                    orderId = order.id,
                    paymentId = null,
                    message = "Payment verification failed"
                )
            }
        } catch (ex: Exception) {
            logger.error("Payment verification error for order: {}", order.orderNumber, ex)
            order.paymentStatus = PaymentStatus.FAILED
            orderRepository.save(order)

            return PaymentVerificationResponse(
                success = false,
                orderId = order.id,
                paymentId = null,
                message = "Payment verification error"
            )
        }
    }
}
