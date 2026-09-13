package com.sweetshop.customer.data.api

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authEventManager: AuthEventManager
) : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth header for login, register, and refresh endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/auth/login") || path.contains("/auth/register") || path.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenManager.getAccessToken() }

        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)

        // If we get a 401, try to refresh the token
        if (response.code == 401 && token != null) {
            response.close()

            synchronized(this) {
                // Double-check: another thread may have already refreshed
                val currentToken = runBlocking { tokenManager.getAccessToken() }
                if (currentToken != null && currentToken != token) {
                    // Token was already refreshed by another thread, retry with new token
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(retryRequest)
                }

                val refreshToken = runBlocking { tokenManager.getRefreshToken() }
                if (refreshToken != null) {
                    val newToken = attemptTokenRefresh(originalRequest, refreshToken)
                    if (newToken != null) {
                        // Retry with new token
                        val retryRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                        return chain.proceed(retryRequest)
                    }
                }

                // Refresh failed - clear tokens and signal session expired
                runBlocking { tokenManager.clearTokens() }
                authEventManager.emitSessionExpired()
            }

            return chain.proceed(originalRequest)
        }

        return response
    }

    private fun attemptTokenRefresh(originalRequest: okhttp3.Request, refreshToken: String): String? {
        return try {
            val baseUrl = originalRequest.url.newBuilder()
                .encodedPath("/api/v1/auth/refresh")
                .build()

            val refreshBody = gson.toJson(mapOf("refreshToken" to refreshToken))
                .toRequestBody("application/json".toMediaType())

            val refreshRequest = Request.Builder()
                .url(baseUrl)
                .post(refreshBody)
                .build()

            val client = OkHttpClient.Builder().build()
            val refreshResponse = client.newCall(refreshRequest).execute()

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body?.string()
                val authResponse = gson.fromJson(body, TokenRefreshResponse::class.java)
                if (authResponse?.data != null) {
                    runBlocking {
                        tokenManager.saveTokens(
                            authResponse.data.accessToken,
                            authResponse.data.refreshToken
                        )
                    }
                    authResponse.data.accessToken
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

private data class TokenRefreshData(
    val accessToken: String = "",
    val refreshToken: String = ""
)

private data class TokenRefreshResponse(
    val success: Boolean = false,
    val data: TokenRefreshData? = null
)
