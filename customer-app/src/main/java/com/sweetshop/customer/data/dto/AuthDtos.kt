package com.sweetshop.customer.data.dto

import com.google.gson.annotations.SerializedName
import com.sweetshop.customer.domain.model.User

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String = "",
    @SerializedName("refreshToken") val refreshToken: String = "",
    @SerializedName("tokenType") val tokenType: String = "Bearer",
    @SerializedName("expiresIn") val expiresIn: Long = 0,
    @SerializedName("user") val user: UserDto? = null
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class UserDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("role") val role: String = "CUSTOMER",
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        profileImageUrl = profileImageUrl,
        role = role,
        isActive = isActive,
        createdAt = createdAt
    )
}
