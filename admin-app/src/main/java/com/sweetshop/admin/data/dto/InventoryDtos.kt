package com.sweetshop.admin.data.dto

data class InventoryItemDto(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val status: String
)
