package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.CategoryDto
import com.sweetshop.admin.data.dto.CreateCategoryRequest
import com.sweetshop.admin.data.dto.UpdateCategoryRequest
import com.sweetshop.admin.util.Resource

interface CategoryRepository {
    suspend fun getCategories(): Resource<List<CategoryDto>>
    suspend fun createCategory(request: CreateCategoryRequest): Resource<CategoryDto>
    suspend fun updateCategory(id: Long, request: UpdateCategoryRequest): Resource<CategoryDto>
    suspend fun deleteCategory(id: Long): Resource<Unit>
}
