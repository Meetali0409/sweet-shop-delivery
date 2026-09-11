package com.sweetshop.backend.service

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.entity.Coupon
import com.sweetshop.backend.entity.DiscountType
import com.sweetshop.backend.exception.InvalidCouponException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.CouponRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponRepository: CouponRepository
) {
    private val logger = LoggerFactory.getLogger(CouponService::class.java)

    fun validateCoupon(code: String, orderTotal: BigDecimal): CouponValidationResponse {
        val coupon = couponRepository.findByCouponCode(code.uppercase())
            ?: return CouponValidationResponse(
                isValid = false,
                message = "Coupon code not found"
            )

        val validationMessage = validateCouponRules(coupon, orderTotal)
        if (validationMessage != null) {
            return CouponValidationResponse(
                isValid = false,
                message = validationMessage
            )
        }

        val discount = calculateDiscount(coupon, orderTotal)
        return CouponValidationResponse(
            isValid = true,
            discount = discount,
            message = "Coupon applied successfully! You save ${discount.setScale(2, RoundingMode.HALF_UP)}"
        )
    }

    @Transactional
    fun applyCoupon(code: String, orderTotal: BigDecimal): BigDecimal {
        val coupon = couponRepository.findByCouponCode(code.uppercase())
            ?: throw InvalidCouponException("Coupon code not found: $code")

        val validationMessage = validateCouponRules(coupon, orderTotal)
        if (validationMessage != null) {
            throw InvalidCouponException(validationMessage)
        }

        val discount = calculateDiscount(coupon, orderTotal)

        coupon.usageCount++
        couponRepository.save(coupon)

        logger.info("Coupon applied: code={}, discount={}", code, discount)
        return discount
    }

    fun getCoupons(): List<CouponDto> {
        return couponRepository.findAll().map { it.toDto() }
    }

    fun getActiveCoupons(): List<CouponDto> {
        return couponRepository.findByIsActiveTrue()
            .filter { coupon ->
                val now = LocalDateTime.now()
                (coupon.validFrom == null || !now.isBefore(coupon.validFrom)) &&
                (coupon.validUntil == null || !now.isAfter(coupon.validUntil)) &&
                (coupon.usageLimit == null || coupon.usageCount < coupon.usageLimit!!)
            }
            .map { it.toDto() }
    }

    @Transactional
    fun createCoupon(request: CreateCouponRequest): CouponDto {
        val coupon = Coupon(
            couponCode = request.couponCode.uppercase(),
            description = request.description,
            discountType = request.discountType,
            discountValue = request.discountValue,
            minimumOrderValue = request.minimumOrderValue,
            maximumDiscount = request.maximumDiscount,
            validFrom = request.validFrom,
            validUntil = request.validUntil,
            usageLimit = request.usageLimit,
            isActive = request.isActive
        )

        val savedCoupon = couponRepository.save(coupon)
        logger.info("Coupon created: id={}, code={}", savedCoupon.id, savedCoupon.couponCode)
        return savedCoupon.toDto()
    }

    @Transactional
    fun updateCoupon(id: Long, request: UpdateCouponRequest): CouponDto {
        val coupon = couponRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Coupon not found with id: $id") }

        request.couponCode?.let { coupon.couponCode = it.uppercase() }
        request.description?.let { coupon.description = it }
        request.discountType?.let { coupon.discountType = it }
        request.discountValue?.let { coupon.discountValue = it }
        request.minimumOrderValue?.let { coupon.minimumOrderValue = it }
        request.maximumDiscount?.let { coupon.maximumDiscount = it }
        request.validFrom?.let { coupon.validFrom = it }
        request.validUntil?.let { coupon.validUntil = it }
        request.usageLimit?.let { coupon.usageLimit = it }
        request.isActive?.let { coupon.isActive = it }

        val savedCoupon = couponRepository.save(coupon)
        logger.info("Coupon updated: id={}, code={}", savedCoupon.id, savedCoupon.couponCode)
        return savedCoupon.toDto()
    }

    @Transactional
    fun deleteCoupon(id: Long) {
        val coupon = couponRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Coupon not found with id: $id") }
        couponRepository.delete(coupon)
        logger.info("Coupon deleted: id={}", id)
    }

    private fun validateCouponRules(coupon: Coupon, orderTotal: BigDecimal): String? {
        if (!coupon.isActive) {
            return "This coupon is no longer active"
        }

        val now = LocalDateTime.now()
        if (coupon.validFrom != null && now.isBefore(coupon.validFrom)) {
            return "This coupon is not yet valid"
        }

        if (coupon.validUntil != null && now.isAfter(coupon.validUntil)) {
            return "This coupon has expired"
        }

        if (coupon.usageLimit != null && coupon.usageCount >= coupon.usageLimit!!) {
            return "This coupon has reached its usage limit"
        }

        if (coupon.minimumOrderValue != null && orderTotal < coupon.minimumOrderValue!!) {
            return "Minimum order value of ${coupon.minimumOrderValue} is required for this coupon"
        }

        return null
    }

    private fun calculateDiscount(coupon: Coupon, orderTotal: BigDecimal): BigDecimal {
        var discount = when (coupon.discountType) {
            DiscountType.PERCENTAGE -> {
                orderTotal.multiply(coupon.discountValue)
                    .divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            }
            DiscountType.FIXED -> coupon.discountValue
        }

        if (coupon.maximumDiscount != null && discount > coupon.maximumDiscount!!) {
            discount = coupon.maximumDiscount!!
        }

        if (discount > orderTotal) {
            discount = orderTotal
        }

        return discount.setScale(2, RoundingMode.HALF_UP)
    }
}
