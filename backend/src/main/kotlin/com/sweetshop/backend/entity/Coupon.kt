package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "coupons")
class Coupon(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "coupon_code", nullable = false, unique = true)
    var couponCode: String,

    @Column(name = "description")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    var discountType: DiscountType,

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    var discountValue: BigDecimal,

    @Column(name = "minimum_order_value", precision = 10, scale = 2)
    var minimumOrderValue: BigDecimal? = null,

    @Column(name = "maximum_discount", precision = 10, scale = 2)
    var maximumDiscount: BigDecimal? = null,

    @Column(name = "valid_from")
    var validFrom: LocalDateTime? = null,

    @Column(name = "valid_until")
    var validUntil: LocalDateTime? = null,

    @Column(name = "usage_limit")
    var usageLimit: Int? = null,

    @Column(name = "usage_count", nullable = false)
    var usageCount: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coupon) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Coupon(id=$id, code=$couponCode)"
}
