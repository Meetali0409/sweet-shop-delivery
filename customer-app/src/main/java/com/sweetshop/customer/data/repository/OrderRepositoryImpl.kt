package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.data.dto.CancelOrderRequest
import com.sweetshop.customer.data.dto.CreateOrderRequest
import com.sweetshop.customer.domain.model.Order
import com.sweetshop.customer.domain.repository.OrderRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val api: SweetShopApi
) : OrderRepository {

    override suspend fun createOrder(
        addressId: Long,
        paymentMethod: String,
        notes: String?,
        couponCode: String?
    ): Resource<Order> {
        return try {
            val response = api.createOrder(
                CreateOrderRequest(addressId, paymentMethod, notes, couponCode)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to create order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getOrders(status: String?, page: Int, size: Int): Resource<List<Order>> {
        return try {
            val response = api.getOrders(status, page, size)
            if (response.isSuccessful && response.body()?.success == true) {
                val orders = response.body()!!.data!!.content.map { it.toDomain() }
                Resource.Success(orders)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getOrderById(orderId: Long): Resource<Order> {
        return try {
            val response = api.getOrderById(orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun cancelOrder(orderId: Long, reason: String): Resource<Order> {
        return try {
            val response = api.cancelOrder(orderId, CancelOrderRequest(reason))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to cancel order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun reorder(orderId: Long): Resource<Order> {
        return try {
            val response = api.reorder(orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to reorder")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
