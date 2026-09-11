package com.sweetshop.backend.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val categoryId: Long,
    val categoryName: String,
    val imageUrl: String?,
    val price: BigDecimal,
    val discountPrice: BigDecimal?,
    val unit: String?,
    val stockQuantity: Int,
    val minimumOrderQuantity: Int,
    val isAvailable: Boolean,
    val isFeatured: Boolean,
    val isBestseller: Boolean,
    val rating: BigDecimal,
    val totalReviews: Int,
    val ingredients: String?,
    val allergenInfo: String?,
    val weights: List<ProductWeightDto>,
    val isInWishlist: Boolean = false
)

data class ProductListDto(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val price: BigDecimal,
    val discountPrice: BigDecimal?,
    val unit: String?,
    val rating: BigDecimal,
    val totalReviews: Int,
    val isAvailable: Boolean,
    val isBestseller: Boolean,
    val categoryName: String
)

data class ProductWeightDto(
    val id: Long,
    val weight: String,
    val price: BigDecimal,
    val discountPrice: BigDecimal?
)

data class CreateProductRequest(
    @field:NotBlank(message = "Product name is required")
    val name: String,

    val description: String? = null,

    @field:NotNull(message = "Category ID is required")
    val categoryId: Long,

    val imageUrl: String? = null,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    val price: BigDecimal,

    val discountPrice: BigDecimal? = null,

    val unit: String? = null,

    @field:Min(value = 0, message = "Stock quantity cannot be negative")
    val stockQuantity: Int = 0,

    @field:Min(value = 1, message = "Minimum order quantity must be at least 1")
    val minimumOrderQuantity: Int = 1,

    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val ingredients: String? = null,
    val allergenInfo: String? = null,
    val weights: List<CreateProductWeightRequest>? = null
)

data class CreateProductWeightRequest(
    @field:NotBlank(message = "Weight is required")
    val weight: String,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    val price: BigDecimal,

    val discountPrice: BigDecimal? = null
)

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val categoryId: Long? = null,
    val imageUrl: String? = null,
    val price: BigDecimal? = null,
    val discountPrice: BigDecimal? = null,
    val unit: String? = null,
    val stockQuantity: Int? = null,
    val minimumOrderQuantity: Int? = null,
    val isAvailable: Boolean? = null,
    val isFeatured: Boolean? = null,
    val isBestseller: Boolean? = null,
    val ingredients: String? = null,
    val allergenInfo: String? = null,
    val weights: List<CreateProductWeightRequest>? = null
)
