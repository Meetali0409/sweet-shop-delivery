package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(name = "discount_price", precision = 10, scale = 2)
    var discountPrice: BigDecimal? = null,

    @Column(name = "unit")
    var unit: String? = null,

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int = 0,

    @Column(name = "minimum_order_quantity", nullable = false)
    var minimumOrderQuantity: Int = 1,

    @Column(name = "is_available", nullable = false)
    var isAvailable: Boolean = true,

    @Column(name = "is_featured", nullable = false)
    var isFeatured: Boolean = false,

    @Column(name = "is_bestseller", nullable = false)
    var isBestseller: Boolean = false,

    @Column(name = "rating", precision = 3, scale = 2)
    var rating: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_reviews", nullable = false)
    var totalReviews: Int = 0,

    @Column(name = "ingredients", columnDefinition = "TEXT")
    var ingredients: String? = null,

    @Column(name = "allergen_info")
    var allergenInfo: String? = null,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    var weights: MutableList<ProductWeight> = mutableListOf(),

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
        if (other !is Product) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Product(id=$id, name=$name, price=$price)"
}
