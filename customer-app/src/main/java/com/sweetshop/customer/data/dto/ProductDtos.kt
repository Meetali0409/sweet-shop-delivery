package com.sweetshop.customer.data.dto

import com.google.gson.annotations.SerializedName
import com.sweetshop.customer.domain.model.Category
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.model.ProductVariant

data class ProductDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("shortDescription") val shortDescription: String = "",
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("categoryId") val categoryId: Long = 0,
    @SerializedName("categoryName") val categoryName: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("discountPrice") val discountPrice: Double? = null,
    @SerializedName("discountPercentage") val discountPercentage: Double = 0.0,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("totalReviews") val totalReviews: Int = 0,
    @SerializedName("ingredients") val ingredients: String = "",
    @SerializedName("allergenInfo") val allergenInfo: String = "",
    @SerializedName("nutritionalInfo") val nutritionalInfo: String = "",
    @SerializedName("shelfLife") val shelfLife: String = "",
    @SerializedName("isVegetarian") val isVegetarian: Boolean = true,
    @SerializedName("isAvailable") val isAvailable: Boolean = true,
    @SerializedName("isFeatured") val isFeatured: Boolean = false,
    @SerializedName("isBestseller") val isBestseller: Boolean = false,
    @SerializedName("weights") val weights: List<ProductVariantDto>? = null,
    @SerializedName("unit") val unit: String = "",
    @SerializedName("stockQuantity") val stockQuantity: Int = 0,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        description = description,
        shortDescription = shortDescription.ifBlank { description.take(100) },
        imageUrl = imageUrl,
        images = images ?: emptyList(),
        categoryId = categoryId,
        categoryName = categoryName,
        basePrice = price,
        discountedPrice = discountPrice,
        discountPercentage = discountPercentage,
        rating = rating,
        reviewCount = totalReviews,
        ingredients = ingredients,
        allergenInfo = allergenInfo,
        nutritionalInfo = nutritionalInfo,
        shelfLife = shelfLife,
        isVegetarian = isVegetarian,
        isAvailable = isAvailable,
        isFeatured = isFeatured,
        isBestseller = isBestseller,
        variants = weights?.map { it.toDomain() } ?: emptyList(),
        tags = tags ?: emptyList(),
        createdAt = createdAt
    )
}

data class ProductVariantDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("weight") val weight: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("discountPrice") val discountedPrice: Double? = null,
    @SerializedName("isAvailable") val isAvailable: Boolean = true,
    @SerializedName("sku") val sku: String = ""
) {
    fun toDomain(): ProductVariant = ProductVariant(
        id = id,
        weight = weight,
        price = price,
        discountedPrice = discountedPrice,
        isAvailable = isAvailable,
        sku = sku
    )
}

data class CategoryDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("productCount") val productCount: Int = 0,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("sortOrder") val sortOrder: Int = 0
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        productCount = productCount,
        isActive = isActive,
        sortOrder = sortOrder
    )
}
