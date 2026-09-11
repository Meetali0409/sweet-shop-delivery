package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.data.api.TokenManager
import com.sweetshop.customer.data.dto.LoginRequest
import com.sweetshop.customer.data.dto.RefreshTokenRequest
import com.sweetshop.customer.data.dto.RegisterRequest
import com.sweetshop.customer.domain.model.User
import com.sweetshop.customer.domain.repository.AuthRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: SweetShopApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!.data!!
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                authResponse.user?.let { user ->
                    tokenManager.saveUserInfo(user.id, user.name, user.email)
                    Resource.Success(user.toDomain())
                } ?: Resource.Success(User())
            } else {
                val errorMsg = response.body()?.error ?: response.message() ?: "Login failed"
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Resource<User> {
        return try {
            val response = api.register(RegisterRequest(name, email, phone, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!.data!!
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                authResponse.user?.let { user ->
                    tokenManager.saveUserInfo(user.id, user.name, user.email)
                    Resource.Success(user.toDomain())
                } ?: Resource.Success(User())
            } else {
                val errorMsg = response.body()?.error ?: response.message() ?: "Registration failed"
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun refreshToken(): Resource<Boolean> {
        return try {
            val refreshToken = tokenManager.getRefreshToken() ?: return Resource.Error("No refresh token")
            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!.data!!
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                Resource.Success(true)
            } else {
                tokenManager.clearTokens()
                Resource.Error("Token refresh failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    override suspend fun getCurrentUser(): Resource<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.data!!.toDomain()
                Resource.Success(user)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to get user")
            }
        } catch (e: Exception) {
            // Return cached user info if available
            val name = tokenManager.getUserName()
            val email = tokenManager.getUserEmail()
            val id = tokenManager.getUserId()
            if (name != null && email != null && id != null) {
                Resource.Success(User(id = id, name = name, email = email))
            } else {
                Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }
}
