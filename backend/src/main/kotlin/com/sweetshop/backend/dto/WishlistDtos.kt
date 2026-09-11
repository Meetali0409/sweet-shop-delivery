package com.sweetshop.backend.dto

import java.time.LocalDateTime

data class WishlistItemDto(
    val id: Long,
    val product: ProductListDto,
    val addedAt: LocalDateTime
)
