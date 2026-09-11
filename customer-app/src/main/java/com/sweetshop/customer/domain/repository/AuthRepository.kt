package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.User
import com.sweetshop.customer.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(name: String, email: String, phone: String, password: String): Resource<User>
    suspend fun refreshToken(): Resource<Boolean>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): Resource<User>
}
