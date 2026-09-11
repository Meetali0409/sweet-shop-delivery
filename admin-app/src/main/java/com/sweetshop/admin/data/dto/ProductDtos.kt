package com.sweetshop.admin.data.dto

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val categoryId: Long,
    val categoryName: String?,
    val imageUrl: String?,
    val price: Double,
    val discountPrice: Double?,
    val unit: String,
    val stockQuantity: Int,
    val minimumOrderQuantity: Int,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val isBestseller: Boolean,
    val rating: Double,
    val totalReviews: Int,
    val ingredients: String?,
    val allergenInfo: String?,
    val weights: List<ProductWeightDto>?
)

data class ProductListDto(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val discountPrice: Double?,
    val unit: String,
    val rating: Double,
    val totalReviews: Int,
    val isAvailable: Boolean,
    val isBestseller: Boolean,
    val categoryName: String?
)

data class ProductWeightDto(
    val id: Long,
    val weight: String,
    val price: Double,
    val discountPrice: Double?
)

data class CreateProductRequest(
    val name: String,
    val description: String?,
    val categoryId: Long,
    val price: Double,
    val discountPrice: Double?,
    val unit: String,
    val stockQuantity: Int,
    val minimumOrderQuantity: Int = 1,
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val ingredients: String?,
    val allergenInfo: String?
)

data class UpdateProductRequest(
    val name: String?,
    val description: String?,
    val categoryId: Long?,
    val price: Double?,
    val discountPrice: Double?,
    val unit: String?,
    val stockQuantity: Int?,
    val minimumOrderQuantity: Int?,
    val isAvailable: Boolean?,
    val isFeatured: Boolean?,
    val isBestseller: Boolean?,
    val ingredients: String?,
    val allergenInfo: String?
)

data class UpdateStockRequest(
    val quantity: Int
)
