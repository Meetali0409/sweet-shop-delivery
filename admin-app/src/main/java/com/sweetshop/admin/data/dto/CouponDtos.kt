package com.sweetshop.admin.data.dto

data class CouponDto(
    val id: Long,
    val couponCode: String,
    val description: String?,
    val discountType: String,
    val discountValue: Double,
    val minimumOrderValue: Double?,
    val maximumDiscount: Double?,
    val validFrom: String?,
    val validUntil: String?,
    val usageLimit: Int?,
    val usageCount: Int,
    val isActive: Boolean
)

data class CreateCouponRequest(
    val couponCode: String,
    val description: String?,
    val discountType: String,
    val discountValue: Double,
    val minimumOrderValue: Double,
    val maximumDiscount: Double?,
    val validFrom: String,
    val validUntil: String,
    val usageLimit: Int?,
    val isActive: Boolean = true
)

data class UpdateCouponRequest(
    val description: String?,
    val discountValue: Double?,
    val minimumOrderValue: Double?,
    val maximumDiscount: Double?,
    val validUntil: String?,
    val usageLimit: Int?,
    val isActive: Boolean?
)
