package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.AuthResponse
import com.sweetshop.admin.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthResponse>
    suspend fun logout()
    fun isLoggedIn(): Boolean
}
