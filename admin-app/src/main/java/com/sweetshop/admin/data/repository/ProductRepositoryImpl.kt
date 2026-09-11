package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.CreateProductRequest
import com.sweetshop.admin.data.dto.PagedResponse
import com.sweetshop.admin.data.dto.ProductDto
import com.sweetshop.admin.data.dto.ProductListDto
import com.sweetshop.admin.data.dto.UpdateProductRequest
import com.sweetshop.admin.data.dto.UpdateStockRequest
import com.sweetshop.admin.domain.repository.ProductRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : ProductRepository {

    override suspend fun getProducts(
        page: Int,
        size: Int,
        search: String?,
        categoryId: Long?
    ): Resource<PagedResponse<ProductListDto>> {
        return try {
            val response = api.getProducts(page, size, search, categoryId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load products")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getProduct(id: Long): Resource<ProductDto> {
        return try {
            val response = api.getProduct(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun createProduct(request: CreateProductRequest): Resource<ProductDto> {
        return try {
            val response = api.createProduct(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to create product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateProduct(id: Long, request: UpdateProductRequest): Resource<ProductDto> {
        return try {
            val response = api.updateProduct(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to update product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun deleteProduct(id: Long): Resource<Unit> {
        return try {
            val response = api.deleteProduct(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to delete product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateStock(id: Long, quantity: Int): Resource<ProductDto> {
        return try {
            val response = api.updateStock(id, UpdateStockRequest(quantity))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to update stock")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
