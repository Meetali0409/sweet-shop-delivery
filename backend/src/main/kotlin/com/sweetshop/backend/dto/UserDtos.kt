package com.sweetshop.backend.dto

import com.sweetshop.backend.entity.UserRole
import java.math.BigDecimal
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val profileImage: String?,
    val role: UserRole,
    val isActive: Boolean,
    val createdAt: LocalDateTime
)

data class UserProfileUpdateRequest(
    val name: String? = null,
    val phone: String? = null,
    val profileImage: String? = null
)

data class CustomerListDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val totalOrders: Long,
    val totalSpending: BigDecimal,
    val isActive: Boolean,
    val createdAt: LocalDateTime
)
