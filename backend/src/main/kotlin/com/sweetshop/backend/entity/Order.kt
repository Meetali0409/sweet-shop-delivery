package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "order_number", nullable = false, unique = true)
    var orderNumber: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "address_id", nullable = false)
    var addressId: Long,

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal,

    @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    var discount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "delivery_charge", nullable = false, precision = 10, scale = 2)
    var deliveryCharge: BigDecimal = BigDecimal.ZERO,

    @Column(name = "tax", nullable = false, precision = 10, scale = 2)
    var tax: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    var totalAmount: BigDecimal,

    @Column(name = "payment_method")
    var paymentMethod: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    var orderStatus: OrderStatus = OrderStatus.PLACED,

    @Column(name = "coupon_code")
    var couponCode: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

    @Column(name = "estimated_delivery")
    var estimatedDelivery: LocalDate? = null,

    @Column(name = "razorpay_order_id")
    var razorpayOrderId: String? = null,

    @Column(name = "razorpay_payment_id")
    var razorpayPaymentId: String? = null,

    @Column(name = "razorpay_signature")
    var razorpaySignature: String? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf(),

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
        if (other !is Order) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Order(id=$id, orderNumber=$orderNumber, status=$orderStatus)"
}
