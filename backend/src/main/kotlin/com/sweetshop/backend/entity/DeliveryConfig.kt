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

    @Column(name = "shop_latitude", precision = 10, scale = 7)
    var shopLatitude: BigDecimal? = null,

    @Column(name = "shop_longitude", precision = 10, scale = 7)
    var shopLongitude: BigDecimal? = null,

    @Column(name = "delivery_radius_km", precision = 10, scale = 2)
    var deliveryRadiusKm: BigDecimal? = BigDecimal("15.00"),

    @Column(name = "per_km_charge", precision = 10, scale = 2)
    var perKmCharge: BigDecimal? = BigDecimal("5.00"),

    @Column(name = "base_delivery_distance_km", precision = 10, scale = 2)
    var baseDeliveryDistanceKm: BigDecimal? = BigDecimal("3.00"),

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
