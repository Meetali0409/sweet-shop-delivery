package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.CouponService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/coupons")
class CouponController(
    private val couponService: CouponService
) {

    @GetMapping
    fun getActiveCoupons(): ResponseEntity<ApiResponse<List<CouponDto>>> {
        val coupons = couponService.getActiveCoupons()
        return ResponseEntity.ok(ApiResponse(success = true, data = coupons))
    }

    @PostMapping("/validate")
    fun validateCoupon(
        @Valid @RequestBody request: ApplyCouponRequest
    ): ResponseEntity<ApiResponse<CouponValidationResponse>> {
        val response = couponService.validateCoupon(request.couponCode, java.math.BigDecimal.ZERO)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }
}
