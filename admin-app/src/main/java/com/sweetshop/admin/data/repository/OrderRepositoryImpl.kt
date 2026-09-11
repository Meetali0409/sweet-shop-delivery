package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.OrderDto
import com.sweetshop.admin.data.dto.OrderListDto
import com.sweetshop.admin.data.dto.PagedResponse
import com.sweetshop.admin.data.dto.UpdateOrderStatusRequest
import com.sweetshop.admin.domain.repository.OrderRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : OrderRepository {

    override suspend fun getOrders(
        status: String?,
        page: Int,
        size: Int
    ): Resource<PagedResponse<OrderListDto>> {
        return try {
            val response = api.getOrders(status, page, size)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getOrder(id: Long): Resource<OrderDto> {
        return try {
            val response = api.getOrder(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateOrderStatus(id: Long, status: String): Resource<OrderDto> {
        return try {
            val response = api.updateOrderStatus(id, UpdateOrderStatusRequest(status))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to update order status")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
