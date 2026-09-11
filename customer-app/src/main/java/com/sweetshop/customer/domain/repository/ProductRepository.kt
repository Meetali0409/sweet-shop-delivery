package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.Category
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.util.Resource

interface ProductRepository {
    suspend fun getProducts(
        categoryId: Long? = null,
        page: Int = 0,
        size: Int = 20,
        sort: String? = null,
        search: String? = null
    ): Resource<List<Product>>

    suspend fun getProductById(productId: Long): Resource<Product>
    suspend fun getFeaturedProducts(): Resource<List<Product>>
    suspend fun getBestsellers(): Resource<List<Product>>
    suspend fun getNewArrivals(): Resource<List<Product>>
    suspend fun searchProducts(query: String): Resource<List<Product>>
    suspend fun getCategories(): Resource<List<Category>>
}
