package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.api.TokenManager
import com.sweetshop.admin.data.dto.AuthResponse
import com.sweetshop.admin.data.dto.LoginRequest
import com.sweetshop.admin.domain.repository.AuthRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AdminApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    val authResponse = body.data
                    if (authResponse.user.role != "ADMIN") {
                        Resource.Error("Access denied. Admin privileges required.")
                    } else {
                        tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                        tokenManager.saveUserInfo(
                            authResponse.user.name,
                            authResponse.user.email,
                            authResponse.user.role
                        )
                        Resource.Success(authResponse)
                    }
                } else {
                    Resource.Error(body?.message ?: "Login failed")
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Invalid email or password"
                    403 -> "Access denied"
                    else -> "Login failed: ${response.message()}"
                }
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}
