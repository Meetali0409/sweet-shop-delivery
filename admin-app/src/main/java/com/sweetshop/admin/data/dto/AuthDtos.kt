package com.sweetshop.admin.data.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val isActive: Boolean
)
