package com.sweetshop.admin.data.dto

data class CategoryDto(
    val id: Long,
    val name: String,
    val description: String?,
    val image: String?,
    val isActive: Boolean,
    val sortOrder: Int,
    val productCount: Long?
)

data class CreateCategoryRequest(
    val name: String,
    val description: String?,
    val image: String?
)

data class UpdateCategoryRequest(
    val name: String?,
    val description: String?,
    val image: String?,
    val isActive: Boolean?,
    val sortOrder: Int?
)
