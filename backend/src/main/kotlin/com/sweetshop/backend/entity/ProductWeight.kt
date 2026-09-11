package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "product_weights")
class ProductWeight(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(name = "weight", nullable = false)
    var weight: String,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(name = "discount_price", precision = 10, scale = 2)
    var discountPrice: BigDecimal? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductWeight) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "ProductWeight(id=$id, weight=$weight, price=$price)"
}
