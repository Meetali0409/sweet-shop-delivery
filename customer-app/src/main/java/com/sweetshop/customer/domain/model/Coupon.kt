package com.sweetshop.customer.domain.model

data class Coupon(
    val id: Long = 0,
    val code: String = "",
    val description: String = "",
    val discountType: String = "PERCENTAGE",
    val discountValue: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val maxDiscount: Double = 0.0,
    val isActive: Boolean = true,
    val expiresAt: String? = null
)
