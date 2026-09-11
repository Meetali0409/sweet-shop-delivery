package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "delivery_config")
class DeliveryConfig(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "delivery_charge", nullable = false, precision = 10, scale = 2)
    var deliveryCharge: BigDecimal = BigDecimal.ZERO,

    @Column(name = "free_delivery_threshold", precision = 10, scale = 2)
    var freeDeliveryThreshold: BigDecimal? = null,

    @Column(name = "estimated_delivery_days", nullable = false)
    var estimatedDeliveryDays: Int = 3,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeliveryConfig) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "DeliveryConfig(id=$id, charge=$deliveryCharge)"
}
