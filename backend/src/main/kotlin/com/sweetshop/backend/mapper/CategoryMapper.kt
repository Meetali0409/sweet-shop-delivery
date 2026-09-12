package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.CategoryDto
import com.sweetshop.backend.entity.Category

fun Category.toDto(productCount: Long = 0): CategoryDto = CategoryDto(
    id = this.id,
    name = this.name,
    description = this.description,
    imageUrl = this.image,
    isActive = this.isActive,
    sortOrder = this.sortOrder,
    productCount = productCount
)
