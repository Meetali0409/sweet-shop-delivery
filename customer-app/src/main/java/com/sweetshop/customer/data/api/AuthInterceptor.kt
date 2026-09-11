package com.sweetshop.customer.data.api

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth header for login and register endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/auth/login") || path.contains("/auth/register")) {
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
            val refreshToken = runBlocking { tokenManager.getRefreshToken() }

            if (refreshToken != null) {
                // For now, just clear tokens and let the UI handle re-login
                runBlocking { tokenManager.clearTokens() }
            }

            // Retry with the original request (will fail but trigger login flow)
            return chain.proceed(originalRequest)
        }

        return response
    }
}
