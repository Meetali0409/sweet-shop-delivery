package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.CouponDto
import com.sweetshop.admin.data.dto.CreateCouponRequest
import com.sweetshop.admin.data.dto.UpdateCouponRequest
import com.sweetshop.admin.util.Resource

interface CouponRepository {
    suspend fun getCoupons(): Resource<List<CouponDto>>
    suspend fun createCoupon(request: CreateCouponRequest): Resource<CouponDto>
    suspend fun updateCoupon(id: Long, request: UpdateCouponRequest): Resource<CouponDto>
    suspend fun deleteCoupon(id: Long): Resource<Unit>
}
