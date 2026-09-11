package com.sweetshop.admin.data.dto

data class OrderDto(
    val id: Long,
    val orderNumber: String,
    val userId: Long,
    val customerName: String?,
    val customerPhone: String?,
    val address: AddressDto?,
    val items: List<OrderItemDto>,
    val subtotal: Double,
    val discount: Double,
    val deliveryCharge: Double,
    val tax: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: String,
    val couponCode: String?,
    val notes: String?,
    val estimatedDelivery: String?,
    val createdAt: String
)

data class OrderListDto(
    val id: Long,
    val orderNumber: String,
    val items: List<OrderItemDto>,
    val totalAmount: Double,
    val orderStatus: String,
    val paymentStatus: String,
    val itemCount: Int,
    val createdAt: String,
    val customerName: String?,
    val customerPhone: String?
)

data class OrderItemDto(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productImage: String?,
    val quantity: Int,
    val selectedWeight: String,
    val unitPrice: Double,
    val totalPrice: Double
)

data class AddressDto(
    val id: Long,
    val name: String,
    val phone: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val pincode: String,
    val landmark: String?
)

data class UpdateOrderStatusRequest(
    val status: String
)
