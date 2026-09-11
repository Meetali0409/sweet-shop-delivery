package com.sweetshop.backend.dto

import java.math.BigDecimal

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null,
    val code: String? = null
)

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
)

data class ErrorResponse(
    val success: Boolean = false,
    val code: String,
    val message: String,
    val timestamp: String
)

data class DashboardDto(
    val totalOrders: Long,
    val totalRevenue: BigDecimal,
    val totalCustomers: Long,
    val totalProducts: Long,
    val pendingOrders: Long,
    val recentOrders: List<OrderListDto>,
    val lowStockProducts: List<InventoryItemDto>
)

data class SalesReportDto(
    val revenue: BigDecimal,
    val orders: Long,
    val averageOrderValue: BigDecimal,
    val periodLabel: String
)

data class InventoryItemDto(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val status: StockStatus
)

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}
