package com.sweetshop.backend.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Component
class RateLimitFilter(
    @Value("\${app.rate-limit.auth-requests-per-minute:5}")
    private val authLimit: Int,
    @Value("\${app.rate-limit.api-requests-per-minute:60}")
    private val apiLimit: Int
) : OncePerRequestFilter() {

    private data class RateBucket(
        val count: AtomicInteger = AtomicInteger(0),
        @Volatile var windowStart: Long = System.currentTimeMillis()
    )

    private val authBuckets = ConcurrentHashMap<String, RateBucket>()
    private val apiBuckets = ConcurrentHashMap<String, RateBucket>()
    private val windowMs = 60_000L

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = getClientIp(request)
        val path = request.requestURI

        val isAuthEndpoint = path.startsWith("/api/v1/auth/")
        val isApiEndpoint = path.startsWith("/api/")

        if (isAuthEndpoint) {
            if (!checkRate(clientIp, authBuckets, authLimit)) {
                sendRateLimitResponse(response)
                return
            }
        } else if (isApiEndpoint) {
            if (!checkRate(clientIp, apiBuckets, apiLimit)) {
                sendRateLimitResponse(response)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun checkRate(key: String, buckets: ConcurrentHashMap<String, RateBucket>, limit: Int): Boolean {
        val now = System.currentTimeMillis()
        val bucket = buckets.compute(key) { _, existing ->
            if (existing == null || now - existing.windowStart > windowMs) {
                RateBucket(AtomicInteger(1), now)
            } else {
                existing.count.incrementAndGet()
                existing
            }
        }!!
        return bucket.count.get() <= limit
    }

    private fun sendRateLimitResponse(response: HttpServletResponse) {
        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(
            """{"success":false,"error":{"code":"RATE_LIMITED","message":"Too many requests. Please try again later."}}"""
        )
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (!xForwardedFor.isNullOrBlank()) {
            xForwardedFor.split(",")[0].trim()
        } else {
            request.remoteAddr
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/actuator/") || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")
    }
}
