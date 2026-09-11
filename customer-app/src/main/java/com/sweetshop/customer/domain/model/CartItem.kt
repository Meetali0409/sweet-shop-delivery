package com.sweetshop.customer.domain.model

data class CartItem(
    val id: Long = 0,
    val productId: Long = 0,
    val productName: String = "",
    val productImageUrl: String? = null,
    val variantId: Long? = null,
    val weight: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val discountedPrice: Double? = null
)

data class Cart(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val couponCode: String? = null,
    val couponDiscount: Double = 0.0,
    val itemCount: Int = 0
)
