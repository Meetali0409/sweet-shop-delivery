package com.sweetshop.customer.domain.model

data class Order(
    val id: Long = 0,
    val orderNumber: String = "",
    val status: OrderStatus = OrderStatus.PLACED,
    val items: List<OrderItem> = emptyList(),
    val shippingAddress: Address? = null,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val couponCode: String? = null,
    val paymentMethod: String = "COD",
    val paymentStatus: String = "PENDING",
    val notes: String? = null,
    val estimatedDelivery: String? = null,
    val deliveredAt: String? = null,
    val cancelledAt: String? = null,
    val cancellationReason: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class OrderItem(
    val id: Long = 0,
    val productId: Long = 0,
    val productName: String = "",
    val productImageUrl: String? = null,
    val weight: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)

enum class OrderStatus {
    PLACED,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    fun displayName(): String = when (this) {
        PLACED -> "Order Placed"
        CONFIRMED -> "Confirmed"
        PREPARING -> "Preparing"
        OUT_FOR_DELIVERY -> "Out for Delivery"
        DELIVERED -> "Delivered"
        CANCELLED -> "Cancelled"
    }
}
