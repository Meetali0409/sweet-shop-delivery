package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.InventoryItemDto
import com.sweetshop.admin.data.dto.ProductDto
import com.sweetshop.admin.data.dto.UpdateStockRequest
import com.sweetshop.admin.domain.repository.InventoryRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : InventoryRepository {

    override suspend fun getInventory(): Resource<List<InventoryItemDto>> {
        return try {
            val response = api.getInventory()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load inventory")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateStock(productId: Long, quantity: Int): Resource<ProductDto> {
        return try {
            val response = api.updateInventoryStock(productId, UpdateStockRequest(quantity))
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
