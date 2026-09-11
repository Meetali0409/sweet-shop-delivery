package com.sweetshop.admin.data.dto

data class DashboardDto(
    val totalOrders: Long,
    val totalRevenue: Double,
    val totalCustomers: Long,
    val totalProducts: Long,
    val pendingOrders: Long,
    val recentOrders: List<OrderListDto>,
    val lowStockProducts: List<InventoryItemDto>
)

data class SalesReportDto(
    val revenue: Double,
    val orders: Long,
    val averageOrderValue: Double,
    val periodLabel: String
)
