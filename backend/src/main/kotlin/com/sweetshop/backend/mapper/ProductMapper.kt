package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.CreateProductRequest
import com.sweetshop.backend.dto.ProductDto
import com.sweetshop.backend.dto.ProductListDto
import com.sweetshop.backend.dto.ProductWeightDto
import com.sweetshop.backend.entity.Category
import com.sweetshop.backend.entity.Product
import com.sweetshop.backend.entity.ProductWeight

fun Product.toDto(isInWishlist: Boolean = false): ProductDto = ProductDto(
    id = this.id,
    name = this.name,
    description = this.description,
    categoryId = this.category.id,
    categoryName = this.category.name,
    imageUrl = this.imageUrl,
    price = this.price,
    discountPrice = this.discountPrice,
    unit = this.unit,
    stockQuantity = this.stockQuantity,
    minimumOrderQuantity = this.minimumOrderQuantity,
    isAvailable = this.isAvailable,
    isFeatured = this.isFeatured,
    isBestseller = this.isBestseller,
    rating = this.rating,
    totalReviews = this.totalReviews,
    ingredients = this.ingredients,
    allergenInfo = this.allergenInfo,
    weights = this.weights.map { it.toDto() },
    isInWishlist = isInWishlist
)

fun Product.toListDto(): ProductListDto = ProductListDto(
    id = this.id,
    name = this.name,
    imageUrl = this.imageUrl,
    price = this.price,
    discountPrice = this.discountPrice,
    unit = this.unit,
    rating = this.rating,
    totalReviews = this.totalReviews,
    isAvailable = this.isAvailable,
    isBestseller = this.isBestseller,
    categoryName = this.category.name
)

fun ProductWeight.toDto(): ProductWeightDto = ProductWeightDto(
    id = this.id,
    weight = this.weight,
    price = this.price,
    discountPrice = this.discountPrice
)

fun CreateProductRequest.toEntity(category: Category): Product = Product(
    name = this.name,
    description = this.description,
    category = category,
    imageUrl = this.imageUrl,
    price = this.price,
    discountPrice = this.discountPrice,
    unit = this.unit,
    stockQuantity = this.stockQuantity,
    minimumOrderQuantity = this.minimumOrderQuantity,
    isAvailable = this.isAvailable,
    isFeatured = this.isFeatured,
    isBestseller = this.isBestseller,
    ingredients = this.ingredients,
    allergenInfo = this.allergenInfo
)
