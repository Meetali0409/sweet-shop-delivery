package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.OrderDto
import com.sweetshop.admin.data.dto.OrderListDto
import com.sweetshop.admin.data.dto.PagedResponse
import com.sweetshop.admin.util.Resource

interface OrderRepository {
    suspend fun getOrders(status: String?, page: Int, size: Int): Resource<PagedResponse<OrderListDto>>
    suspend fun getOrder(id: Long): Resource<OrderDto>
    suspend fun updateOrderStatus(id: Long, status: String): Resource<OrderDto>
}
