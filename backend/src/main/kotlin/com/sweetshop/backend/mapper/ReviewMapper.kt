package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.ReviewDto
import com.sweetshop.backend.entity.Review

fun Review.toDto(): ReviewDto = ReviewDto(
    id = this.id,
    userId = this.user.id,
    userName = this.user.name,
    productId = this.product.id,
    orderId = this.orderId,
    rating = this.rating,
    review = this.review,
    createdAt = this.createdAt
)
