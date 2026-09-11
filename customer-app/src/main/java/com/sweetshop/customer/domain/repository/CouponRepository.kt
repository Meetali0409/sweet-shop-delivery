package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.Coupon
import com.sweetshop.customer.util.Resource

interface CouponRepository {
    suspend fun getActiveCoupons(): Resource<List<Coupon>>
    suspend fun validateCoupon(code: String, orderTotal: Double): Resource<Coupon>
}
