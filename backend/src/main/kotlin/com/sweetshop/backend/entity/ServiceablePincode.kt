package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "serviceable_pincodes")
class ServiceablePincode(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "pincode", nullable = false, unique = true)
    var pincode: String,

    @Column(name = "city")
    var city: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "latitude", precision = 10, scale = 7)
    var latitude: BigDecimal? = null,

    @Column(name = "longitude", precision = 10, scale = 7)
    var longitude: BigDecimal? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceablePincode) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "ServiceablePincode(id=$id, pincode=$pincode)"
}
