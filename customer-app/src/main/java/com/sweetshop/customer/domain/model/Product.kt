package com.sweetshop.customer.domain.model

data class Product(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val shortDescription: String = "",
    val imageUrl: String? = null,
    val images: List<String> = emptyList(),
    val categoryId: Long = 0,
    val categoryName: String = "",
    val basePrice: Double = 0.0,
    val discountedPrice: Double? = null,
    val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val ingredients: String = "",
    val allergenInfo: String = "",
    val nutritionalInfo: String = "",
    val shelfLife: String = "",
    val isVegetarian: Boolean = true,
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val variants: List<ProductVariant> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: String? = null
)

data class ProductVariant(
    val id: Long = 0,
    val weight: String = "",
    val price: Double = 0.0,
    val discountedPrice: Double? = null,
    val isAvailable: Boolean = true,
    val sku: String = ""
)
