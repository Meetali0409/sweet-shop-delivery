package com.sweetshop.admin.data.dto

data class CustomerListDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val totalOrders: Long,
    val totalSpending: Double,
    val isActive: Boolean,
    val createdAt: String
)
