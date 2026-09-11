package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = response, message = "Registration successful")
        )
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.login(request)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = response, message = "Login successful")
        )
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.refreshToken(request)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = response, message = "Token refreshed successfully")
        )
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = null,
                message = "If the email exists, a password reset link will be sent"
            )
        )
    }
}
