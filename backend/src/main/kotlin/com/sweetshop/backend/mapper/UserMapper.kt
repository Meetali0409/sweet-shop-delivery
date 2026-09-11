package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.CustomerListDto
import com.sweetshop.backend.dto.UserDto
import com.sweetshop.backend.entity.User
import java.math.BigDecimal

fun User.toDto(): UserDto = UserDto(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    profileImage = this.profileImage,
    role = this.role,
    isActive = this.isActive,
    createdAt = this.createdAt
)

fun User.toCustomerListDto(totalOrders: Long, totalSpending: BigDecimal): CustomerListDto = CustomerListDto(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    totalOrders = totalOrders,
    totalSpending = totalSpending,
    isActive = this.isActive,
    createdAt = this.createdAt
)
