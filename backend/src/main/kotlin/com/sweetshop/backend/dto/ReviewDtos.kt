package com.sweetshop.backend.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ReviewDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val productId: Long,
    val orderId: Long,
    val rating: Int,
    val review: String?,
    val createdAt: LocalDateTime
)

data class CreateReviewRequest(
    @field:NotNull(message = "Order ID is required")
    val orderId: Long,

    @field:NotNull(message = "Rating is required")
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    val rating: Int,

    val review: String? = null
)
