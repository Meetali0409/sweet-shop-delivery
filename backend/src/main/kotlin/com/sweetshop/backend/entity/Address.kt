package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "addresses")
class Address(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "phone", nullable = false)
    var phone: String,

    @Column(name = "address_line1", nullable = false)
    var addressLine1: String,

    @Column(name = "address_line2")
    var addressLine2: String? = null,

    @Column(name = "city", nullable = false)
    var city: String,

    @Column(name = "state", nullable = false)
    var state: String,

    @Column(name = "pincode", nullable = false)
    var pincode: String,

    @Column(name = "landmark")
    var landmark: String? = null,

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = false,

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
        if (other !is Address) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Address(id=$id, city=$city, pincode=$pincode)"
}
