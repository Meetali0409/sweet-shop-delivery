package com.sweetshop.backend.dto

import com.sweetshop.backend.entity.OrderStatus
import com.sweetshop.backend.entity.PaymentStatus
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class OrderDto(
    val id: Long,
    val orderNumber: String,
    val userId: Long,
    val customerName: String,
    val customerPhone: String?,
    val address: AddressDto?,
    val items: List<OrderItemDto>,
    val subtotal: BigDecimal,
    val discount: BigDecimal,
    val deliveryCharge: BigDecimal,
    val tax: BigDecimal,
    val totalAmount: BigDecimal,
    val paymentMethod: String?,
    val paymentStatus: PaymentStatus,
    val orderStatus: OrderStatus,
    val couponCode: String?,
    val notes: String?,
    val estimatedDelivery: LocalDate?,
    val createdAt: LocalDateTime
)

data class OrderListDto(
    val id: Long,
    val orderNumber: String,
    val items: List<OrderItemDto>,
    val totalAmount: BigDecimal,
    val orderStatus: OrderStatus,
    val paymentStatus: PaymentStatus,
    val itemCount: Int,
    val createdAt: LocalDateTime,
    val customerName: String? = null,
    val customerPhone: String? = null
)

data class OrderItemDto(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productImage: String?,
    val quantity: Int,
    val selectedWeight: String?,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal
)

data class CreateOrderRequest(
    @field:NotNull(message = "Address ID is required")
    val addressId: Long,

    val paymentMethod: String? = "COD",
    val couponCode: String? = null,
    val notes: String? = null
)

data class UpdateOrderStatusRequest(
    @field:NotNull(message = "Status is required")
    val status: OrderStatus
)

data class ReorderResponse(
    val addedItems: List<String>,
    val unavailableItems: List<String>,
    val message: String
)
