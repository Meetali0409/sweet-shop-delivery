package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.data.api.ValidateCouponRequest
import com.sweetshop.customer.domain.model.Coupon
import com.sweetshop.customer.domain.repository.CouponRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CouponRepositoryImpl @Inject constructor(
    private val api: SweetShopApi
) : CouponRepository {

    override suspend fun getActiveCoupons(): Resource<List<Coupon>> {
        return try {
            val response = api.getActiveCoupons()
            if (response.isSuccessful && response.body()?.success == true) {
                val coupons = response.body()!!.data!!.map { it.toDomain() }
                Resource.Success(coupons)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load coupons")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun validateCoupon(code: String, orderTotal: Double): Resource<Coupon> {
        return try {
            val response = api.validateCoupon(ValidateCouponRequest(code, orderTotal))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Invalid coupon")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
