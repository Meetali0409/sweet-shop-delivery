package com.sweetshop.customer.data.dto

import com.google.gson.annotations.SerializedName
import com.sweetshop.customer.domain.model.Cart
import com.sweetshop.customer.domain.model.CartItem

data class AddToCartRequest(
    @SerializedName("productId") val productId: Long,
    @SerializedName("selectedWeight") val selectedWeight: String? = null,
    @SerializedName("quantity") val quantity: Int = 1
)

data class UpdateCartItemRequest(
    @SerializedName("quantity") val quantity: Int
)

data class ApplyCouponRequest(
    @SerializedName("couponCode") val couponCode: String
)

data class CartDto(
    @SerializedName("items") val items: List<CartItemDto> = emptyList(),
    @SerializedName("subtotal") val subtotal: Double = 0.0,
    @SerializedName("discount") val discount: Double = 0.0,
    @SerializedName("deliveryFee") val deliveryFee: Double = 0.0,
    @SerializedName("tax") val tax: Double = 0.0,
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("couponCode") val couponCode: String? = null,
    @SerializedName("couponDiscount") val couponDiscount: Double = 0.0,
    @SerializedName("itemCount") val itemCount: Int = 0
) {
    fun toDomain(): Cart = Cart(
        items = items.map { it.toDomain() },
        subtotal = subtotal,
        discount = discount,
        deliveryFee = deliveryFee,
        tax = tax,
        total = total,
        couponCode = couponCode,
        couponDiscount = couponDiscount,
        itemCount = itemCount
    )
}

data class CartItemDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("productId") val productId: Long = 0,
    @SerializedName("productName") val productName: String = "",
    @SerializedName("productImage") val productImage: String? = null,
    @SerializedName("selectedWeight") val selectedWeight: String? = null,
    @SerializedName("quantity") val quantity: Int = 1,
    @SerializedName("unitPrice") val unitPrice: Double = 0.0,
    @SerializedName("totalPrice") val totalPrice: Double = 0.0,
    @SerializedName("isAvailable") val isAvailable: Boolean = true
) {
    fun toDomain(): CartItem = CartItem(
        id = id,
        productId = productId,
        productName = productName,
        productImageUrl = productImage,
        variantId = null,
        weight = selectedWeight ?: "",
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        discountedPrice = null
    )
}
