package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.domain.model.Category
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.repository.ProductRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: SweetShopApi
) : ProductRepository {

    override suspend fun getProducts(
        categoryId: Long?,
        page: Int,
        size: Int,
        sort: String?,
        search: String?
    ): Resource<List<Product>> {
        return try {
            val response = api.getProducts(categoryId, page, size, sort, search)
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data!!.content.map { it.toDomain() }
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load products")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getProductById(productId: Long): Resource<Product> {
        return try {
            val response = api.getProductById(productId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getFeaturedProducts(): Resource<List<Product>> {
        return try {
            val response = api.getFeaturedProducts()
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data!!.content.map { it.toDomain() }
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load featured products")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getBestsellers(): Resource<List<Product>> {
        return try {
            val response = api.getBestsellers()
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data!!.content.map { it.toDomain() }
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load bestsellers")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getNewArrivals(): Resource<List<Product>> {
        return try {
            val response = api.getProducts(page = 0, size = 10, sort = "createdAt,desc")
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data!!.content.map { it.toDomain() }
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load new arrivals")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun searchProducts(query: String): Resource<List<Product>> {
        return try {
            val response = api.getProducts(search = query, page = 0, size = 20)
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data!!.content.map { it.toDomain() }
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.error ?: "Search failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val response = api.getCategories()
            if (response.isSuccessful && response.body()?.success == true) {
                val categories = response.body()!!.data!!.map { it.toDomain() }
                Resource.Success(categories)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load categories")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
