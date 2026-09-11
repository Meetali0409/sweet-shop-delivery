package com.sweetshop.customer.data.dto

import com.google.gson.annotations.SerializedName
import com.sweetshop.customer.domain.model.Order
import com.sweetshop.customer.domain.model.OrderItem
import com.sweetshop.customer.domain.model.OrderStatus

data class CreateOrderRequest(
    @SerializedName("addressId") val addressId: Long,
    @SerializedName("paymentMethod") val paymentMethod: String = "COD",
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("couponCode") val couponCode: String? = null
)

data class CancelOrderRequest(
    @SerializedName("reason") val reason: String
)

data class OrderDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("orderNumber") val orderNumber: String = "",
    @SerializedName("orderStatus") val orderStatus: String = "PLACED",
    @SerializedName("items") val items: List<OrderItemDto> = emptyList(),
    @SerializedName("shippingAddress") val shippingAddress: AddressDto? = null,
    @SerializedName("subtotal") val subtotal: Double = 0.0,
    @SerializedName("discount") val discount: Double = 0.0,
    @SerializedName("deliveryCharge") val deliveryCharge: Double = 0.0,
    @SerializedName("tax") val tax: Double = 0.0,
    @SerializedName("totalAmount") val totalAmount: Double = 0.0,
    @SerializedName("itemCount") val itemCount: Int = 0,
    @SerializedName("couponCode") val couponCode: String? = null,
    @SerializedName("paymentMethod") val paymentMethod: String = "COD",
    @SerializedName("paymentStatus") val paymentStatus: String = "PENDING",
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("estimatedDelivery") val estimatedDelivery: String? = null,
    @SerializedName("deliveredAt") val deliveredAt: String? = null,
    @SerializedName("cancelledAt") val cancelledAt: String? = null,
    @SerializedName("cancellationReason") val cancellationReason: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    fun toDomain(): Order = Order(
        id = id,
        orderNumber = orderNumber,
        status = try { OrderStatus.valueOf(orderStatus) } catch (e: Exception) { OrderStatus.PLACED },
        items = items.map { it.toDomain() },
        shippingAddress = shippingAddress?.toDomain(),
        subtotal = subtotal,
        discount = discount,
        deliveryFee = deliveryCharge,
        tax = tax,
        total = totalAmount,
        couponCode = couponCode,
        paymentMethod = paymentMethod,
        paymentStatus = paymentStatus,
        notes = notes,
        estimatedDelivery = estimatedDelivery,
        deliveredAt = deliveredAt,
        cancelledAt = cancelledAt,
        cancellationReason = cancellationReason,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

data class OrderItemDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("productId") val productId: Long = 0,
    @SerializedName("productName") val productName: String = "",
    @SerializedName("productImage") val productImage: String? = null,
    @SerializedName("selectedWeight") val selectedWeight: String = "",
    @SerializedName("quantity") val quantity: Int = 1,
    @SerializedName("unitPrice") val unitPrice: Double = 0.0,
    @SerializedName("totalPrice") val totalPrice: Double = 0.0
) {
    fun toDomain(): OrderItem = OrderItem(
        id = id,
        productId = productId,
        productName = productName,
        productImageUrl = productImage,
        weight = selectedWeight,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice
    )
}
