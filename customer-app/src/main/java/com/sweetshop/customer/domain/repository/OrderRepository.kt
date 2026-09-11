package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.Order
import com.sweetshop.customer.util.Resource

interface OrderRepository {
    suspend fun createOrder(
        addressId: Long,
        paymentMethod: String = "COD",
        notes: String? = null,
        couponCode: String? = null
    ): Resource<Order>

    suspend fun getOrders(
        status: String? = null,
        page: Int = 0,
        size: Int = 20
    ): Resource<List<Order>>

    suspend fun getOrderById(orderId: Long): Resource<Order>
    suspend fun cancelOrder(orderId: Long, reason: String): Resource<Order>
    suspend fun reorder(orderId: Long): Resource<Order>
}
