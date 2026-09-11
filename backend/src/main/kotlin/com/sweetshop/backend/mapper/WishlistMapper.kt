package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.WishlistItemDto
import com.sweetshop.backend.entity.WishlistItem

fun WishlistItem.toDto(): WishlistItemDto = WishlistItemDto(
    id = this.id,
    product = this.product.toListDto(),
    addedAt = this.createdAt
)
