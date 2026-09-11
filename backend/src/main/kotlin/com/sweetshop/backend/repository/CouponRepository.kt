package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CouponRepository : JpaRepository<Coupon, Long> {
    fun findByCouponCode(code: String): Coupon?
    fun findByIsActiveTrue(): List<Coupon>
}
