package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.CouponService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/coupons")
@PreAuthorize("hasRole('ADMIN')")
class AdminCouponController(
    private val couponService: CouponService
) {

    @GetMapping
    fun getCoupons(): ResponseEntity<ApiResponse<List<CouponDto>>> {
        val coupons = couponService.getCoupons()
        return ResponseEntity.ok(ApiResponse(success = true, data = coupons))
    }

    @PostMapping
    fun createCoupon(
        @Valid @RequestBody request: CreateCouponRequest
    ): ResponseEntity<ApiResponse<CouponDto>> {
        val coupon = couponService.createCoupon(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = coupon, message = "Coupon created successfully")
        )
    }

    @PutMapping("/{id}")
    fun updateCoupon(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCouponRequest
    ): ResponseEntity<ApiResponse<CouponDto>> {
        val coupon = couponService.updateCoupon(id, request)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = coupon, message = "Coupon updated successfully")
        )
    }

    @DeleteMapping("/{id}")
    fun deleteCoupon(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        couponService.deleteCoupon(id)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = null, message = "Coupon deleted successfully")
        )
    }
}
