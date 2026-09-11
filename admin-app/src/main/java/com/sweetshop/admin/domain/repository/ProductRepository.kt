package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.CreateProductRequest
import com.sweetshop.admin.data.dto.PagedResponse
import com.sweetshop.admin.data.dto.ProductDto
import com.sweetshop.admin.data.dto.ProductListDto
import com.sweetshop.admin.data.dto.UpdateProductRequest
import com.sweetshop.admin.util.Resource

interface ProductRepository {
    suspend fun getProducts(page: Int, size: Int, search: String?, categoryId: Long?): Resource<PagedResponse<ProductListDto>>
    suspend fun getProduct(id: Long): Resource<ProductDto>
    suspend fun createProduct(request: CreateProductRequest): Resource<ProductDto>
    suspend fun updateProduct(id: Long, request: UpdateProductRequest): Resource<ProductDto>
    suspend fun deleteProduct(id: Long): Resource<Unit>
    suspend fun updateStock(id: Long, quantity: Int): Resource<ProductDto>
}
