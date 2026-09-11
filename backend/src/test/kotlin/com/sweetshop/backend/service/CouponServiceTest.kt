package com.sweetshop.backend.service

import com.sweetshop.backend.entity.Coupon
import com.sweetshop.backend.entity.DiscountType
import com.sweetshop.backend.exception.InvalidCouponException
import com.sweetshop.backend.repository.CouponRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class CouponServiceTest {

    @Autowired
    private lateinit var couponService: CouponService

    @Autowired
    private lateinit var couponRepository: CouponRepository

    @BeforeEach
    fun setup() {
        couponRepository.deleteAll()

        couponRepository.save(
            Coupon(
                couponCode = "TEST20",
                description = "20% off",
                discountType = DiscountType.PERCENTAGE,
                discountValue = BigDecimal("20.00"),
                minimumOrderValue = BigDecimal("200.00"),
                maximumDiscount = BigDecimal("100.00"),
                validFrom = LocalDateTime.now().minusDays(1),
                validUntil = LocalDateTime.now().plusDays(30),
                usageLimit = 100,
                isActive = true
            )
        )

        couponRepository.save(
            Coupon(
                couponCode = "FLAT50",
                description = "Flat 50 off",
                discountType = DiscountType.FIXED,
                discountValue = BigDecimal("50.00"),
                minimumOrderValue = BigDecimal("300.00"),
                validFrom = LocalDateTime.now().minusDays(1),
                validUntil = LocalDateTime.now().plusDays(30),
                usageLimit = 50,
                isActive = true
            )
        )

        couponRepository.save(
            Coupon(
                couponCode = "EXPIRED",
                description = "Expired coupon",
                discountType = DiscountType.PERCENTAGE,
                discountValue = BigDecimal("10.00"),
                minimumOrderValue = BigDecimal("100.00"),
                validFrom = LocalDateTime.now().minusDays(30),
                validUntil = LocalDateTime.now().minusDays(1),
                isActive = true
            )
        )
    }

    @Test
    fun `validate percentage coupon should return correct discount`() {
        val response = couponService.validateCoupon("TEST20", BigDecimal("500.00"))
        assertTrue(response.isValid)
        assertEquals(BigDecimal("100.00").setScale(2), response.discount?.setScale(2))
    }

    @Test
    fun `validate fixed coupon should return correct discount`() {
        val response = couponService.validateCoupon("FLAT50", BigDecimal("400.00"))
        assertTrue(response.isValid)
        assertEquals(BigDecimal("50.00").setScale(2), response.discount?.setScale(2))
    }

    @Test
    fun `validate coupon with order below minimum should fail`() {
        val response = couponService.validateCoupon("TEST20", BigDecimal("100.00"))
        assertFalse(response.isValid)
    }

    @Test
    fun `validate expired coupon should fail`() {
        val response = couponService.validateCoupon("EXPIRED", BigDecimal("500.00"))
        assertFalse(response.isValid)
    }

    @Test
    fun `validate nonexistent coupon should fail`() {
        val response = couponService.validateCoupon("NONEXISTENT", BigDecimal("500.00"))
        assertFalse(response.isValid)
    }

    @Test
    fun `percentage discount should be capped by maximum discount`() {
        // 20% of 1000 = 200, but max discount is 100
        val response = couponService.validateCoupon("TEST20", BigDecimal("1000.00"))
        assertTrue(response.isValid)
        assertEquals(BigDecimal("100.00").setScale(2), response.discount?.setScale(2))
    }

    @Test
    fun `apply coupon should return discount amount`() {
        val discount = couponService.applyCoupon("TEST20", BigDecimal("500.00"))
        assertEquals(BigDecimal("100.00").setScale(2), discount.setScale(2))
    }

    @Test
    fun `apply invalid coupon should throw exception`() {
        assertThrows<InvalidCouponException> {
            couponService.applyCoupon("NONEXISTENT", BigDecimal("500.00"))
        }
    }
}
