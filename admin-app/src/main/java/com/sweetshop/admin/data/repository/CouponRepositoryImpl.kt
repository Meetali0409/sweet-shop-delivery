package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.CouponDto
import com.sweetshop.admin.data.dto.CreateCouponRequest
import com.sweetshop.admin.data.dto.UpdateCouponRequest
import com.sweetshop.admin.domain.repository.CouponRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CouponRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : CouponRepository {

    override suspend fun getCoupons(): Resource<List<CouponDto>> {
        return try {
            val response = api.getCoupons()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load coupons")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun createCoupon(request: CreateCouponRequest): Resource<CouponDto> {
        return try {
            val response = api.createCoupon(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to create coupon")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateCoupon(id: Long, request: UpdateCouponRequest): Resource<CouponDto> {
        return try {
            val response = api.updateCoupon(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to update coupon")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun deleteCoupon(id: Long): Resource<Unit> {
        return try {
            val response = api.deleteCoupon(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to delete coupon")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
