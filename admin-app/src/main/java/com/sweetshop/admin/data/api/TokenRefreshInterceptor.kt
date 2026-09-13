package com.sweetshop.admin.data.api

import com.google.gson.Gson
import com.sweetshop.admin.data.dto.ApiResponse
import com.sweetshop.admin.data.dto.AuthResponse
import com.sweetshop.admin.data.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authEventManager: AuthEventManager
) : Interceptor {

    private val gson = Gson()

    @Volatile
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // Skip auth endpoints to avoid infinite loops
        if (originalRequest.url.encodedPath.contains("/auth/")) {
            return response
        }

        if (response.code == 401) {
            synchronized(this) {
                // Check if another thread already refreshed the token
                val currentToken = tokenManager.getAccessTokenSync()
                val requestToken = originalRequest.header("Authorization")?.removePrefix("Bearer ")

                if (currentToken != null && currentToken != requestToken) {
                    // Token was already refreshed by another thread, retry with new token
                    response.close()
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(newRequest)
                }

                if (!isRefreshing) {
                    isRefreshing = true
                    val refreshToken = tokenManager.getRefreshTokenSync()

                    if (refreshToken != null) {
                        val refreshed = tryRefreshToken(chain, refreshToken)
                        isRefreshing = false

                        if (refreshed) {
                            response.close()
                            val newToken = tokenManager.getAccessTokenSync()
                            val newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                            return chain.proceed(newRequest)
                        }
                    } else {
                        isRefreshing = false
                    }

                    // Refresh failed - session expired
                    runBlocking { tokenManager.clearAll() }
                    authEventManager.emitSessionExpired()
                } else {
                    // Another thread is already refreshing; retry with the (presumably refreshed) token
                    val refreshedToken = tokenManager.getAccessTokenSync()
                    if (refreshedToken != null) {
                        response.close()
                        val newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $refreshedToken")
                            .build()
                        return chain.proceed(newRequest)
                    }
                }
            }
        }

        return response
    }

    private fun tryRefreshToken(chain: Interceptor.Chain, refreshToken: String): Boolean {
        return try {
            val baseUrl = chain.request().url.toString()
                .substringBefore("/api/v1/") + "/api/v1/"

            val requestBody = gson.toJson(RefreshTokenRequest(refreshToken))
                .toRequestBody("application/json".toMediaType())

            val refreshRequest = Request.Builder()
                .url("${baseUrl}auth/refresh")
                .post(requestBody)
                .build()

            val refreshResponse = chain.proceed(refreshRequest)

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body?.string()
                refreshResponse.close()
                if (body != null) {
                    val apiResponse = gson.fromJson(body, RefreshApiResponse::class.java)
                    if (apiResponse.success && apiResponse.data != null) {
                        runBlocking {
                            tokenManager.saveTokens(
                                apiResponse.data.accessToken,
                                apiResponse.data.refreshToken
                            )
                        }
                        return true
                    }
                }
            } else {
                refreshResponse.close()
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    // Separate class for Gson deserialization (avoids generic type erasure)
    private data class RefreshApiResponse(
        val success: Boolean,
        val data: AuthResponse?,
        val message: String?
    )
}
