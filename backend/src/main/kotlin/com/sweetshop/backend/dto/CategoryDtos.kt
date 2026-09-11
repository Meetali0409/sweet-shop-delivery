package com.sweetshop.backend.dto

import jakarta.validation.constraints.NotBlank

data class CategoryDto(
    val id: Long,
    val name: String,
    val description: String?,
    val image: String?,
    val isActive: Boolean,
    val sortOrder: Int,
    val productCount: Long = 0
)

data class CreateCategoryRequest(
    @field:NotBlank(message = "Category name is required")
    val name: String,

    val description: String? = null,
    val image: String? = null
)

data class UpdateCategoryRequest(
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val isActive: Boolean? = null,
    val sortOrder: Int? = null
)
