package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.InventoryItemDto
import com.sweetshop.admin.data.dto.ProductDto
import com.sweetshop.admin.util.Resource

interface InventoryRepository {
    suspend fun getInventory(): Resource<List<InventoryItemDto>>
    suspend fun updateStock(productId: Long, quantity: Int): Resource<ProductDto>
}
