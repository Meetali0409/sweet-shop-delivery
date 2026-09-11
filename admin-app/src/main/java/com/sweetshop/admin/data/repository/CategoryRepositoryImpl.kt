package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.CategoryDto
import com.sweetshop.admin.data.dto.CreateCategoryRequest
import com.sweetshop.admin.data.dto.UpdateCategoryRequest
import com.sweetshop.admin.domain.repository.CategoryRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : CategoryRepository {

    override suspend fun getCategories(): Resource<List<CategoryDto>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load categories")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun createCategory(request: CreateCategoryRequest): Resource<CategoryDto> {
        return try {
            val response = api.createCategory(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to create category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateCategory(id: Long, request: UpdateCategoryRequest): Resource<CategoryDto> {
        return try {
            val response = api.updateCategory(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to update category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun deleteCategory(id: Long): Resource<Unit> {
        return try {
            val response = api.deleteCategory(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to delete category")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
