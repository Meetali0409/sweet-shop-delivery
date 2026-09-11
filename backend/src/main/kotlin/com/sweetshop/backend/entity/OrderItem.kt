package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(name = "product_name", nullable = false)
    var productName: String,

    @Column(name = "product_image")
    var productImage: String? = null,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "selected_weight")
    var selectedWeight: String? = null,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    var unitPrice: BigDecimal,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderItem) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "OrderItem(id=$id, productName=$productName, quantity=$quantity)"
}
