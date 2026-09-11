package com.sweetshop.backend.dto

import com.sweetshop.backend.entity.DiscountType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class CouponDto(
    val id: Long,
    val couponCode: String,
    val description: String?,
    val discountType: DiscountType,
    val discountValue: BigDecimal,
    val minimumOrderValue: BigDecimal?,
    val maximumDiscount: BigDecimal?,
    val validFrom: LocalDateTime?,
    val validUntil: LocalDateTime?,
    val usageLimit: Int?,
    val usageCount: Int,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CreateCouponRequest(
    @field:NotBlank(message = "Coupon code is required")
    val couponCode: String,

    val description: String? = null,

    @field:NotNull(message = "Discount type is required")
    val discountType: DiscountType,

    @field:NotNull(message = "Discount value is required")
    @field:DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    val discountValue: BigDecimal,

    val minimumOrderValue: BigDecimal? = null,
    val maximumDiscount: BigDecimal? = null,
    val validFrom: LocalDateTime? = null,
    val validUntil: LocalDateTime? = null,
    val usageLimit: Int? = null,
    val isActive: Boolean = true
)

data class UpdateCouponRequest(
    val couponCode: String? = null,
    val description: String? = null,
    val discountType: DiscountType? = null,
    val discountValue: BigDecimal? = null,
    val minimumOrderValue: BigDecimal? = null,
    val maximumDiscount: BigDecimal? = null,
    val validFrom: LocalDateTime? = null,
    val validUntil: LocalDateTime? = null,
    val usageLimit: Int? = null,
    val isActive: Boolean? = null
)

data class ApplyCouponRequest(
    @field:NotBlank(message = "Coupon code is required")
    val couponCode: String
)

data class CouponValidationResponse(
    val isValid: Boolean,
    val discount: BigDecimal = BigDecimal.ZERO,
    val message: String
)
