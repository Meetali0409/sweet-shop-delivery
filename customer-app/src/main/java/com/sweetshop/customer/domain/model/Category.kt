package com.sweetshop.customer.domain.model

data class Category(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val productCount: Int = 0,
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)
