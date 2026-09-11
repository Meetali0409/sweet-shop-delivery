package com.sweetshop.customer.domain.model

data class User(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val role: String = "CUSTOMER",
    val isActive: Boolean = true,
    val createdAt: String? = null
)
