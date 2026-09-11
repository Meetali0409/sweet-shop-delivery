package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.CouponDto
import com.sweetshop.backend.entity.Coupon

fun Coupon.toDto(): CouponDto = CouponDto(
    id = this.id,
    couponCode = this.couponCode,
    description = this.description,
    discountType = this.discountType,
    discountValue = this.discountValue,
    minimumOrderValue = this.minimumOrderValue,
    maximumDiscount = this.maximumDiscount,
    validFrom = this.validFrom,
    validUntil = this.validUntil,
    usageLimit = this.usageLimit,
    usageCount = this.usageCount,
    isActive = this.isActive,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
