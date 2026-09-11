package com.sweetshop.backend.service

import com.sweetshop.backend.dto.AuthResponse
import com.sweetshop.backend.dto.LoginRequest
import com.sweetshop.backend.dto.RefreshTokenRequest
import com.sweetshop.backend.dto.RegisterRequest
import com.sweetshop.backend.entity.RefreshToken
import com.sweetshop.backend.entity.User
import com.sweetshop.backend.entity.UserRole
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.ConflictException
import com.sweetshop.backend.exception.UnauthorizedException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.RefreshTokenRepository
import com.sweetshop.backend.repository.UserRepository
import com.sweetshop.backend.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (request.password != request.confirmPassword) {
            throw BadRequestException("Passwords do not match")
        }

        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already registered")
        }

        val user = User(
            name = request.name,
            email = request.email,
            phone = request.phone,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.CUSTOMER
        )
        val savedUser = userRepository.save(user)
        logger.info("User registered successfully: {}", savedUser.email)

        return generateAuthResponse(savedUser)
    }

    fun login(request: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val user = userRepository.findByEmail(request.email)
            ?: throw UnauthorizedException("Invalid email or password")

        if (!user.isActive) {
            throw UnauthorizedException("Account is deactivated. Please contact support.")
        }

        logger.info("User logged in successfully: {}", user.email)
        return generateAuthResponse(user)
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        if (!jwtTokenProvider.validateToken(request.refreshToken)) {
            throw UnauthorizedException("Invalid or expired refresh token")
        }

        val storedToken = refreshTokenRepository.findByToken(request.refreshToken)
            ?: throw UnauthorizedException("Refresh token not found")

        if (storedToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken)
            throw UnauthorizedException("Refresh token has expired")
        }

        val user = storedToken.user
        refreshTokenRepository.delete(storedToken)

        logger.info("Token refreshed for user: {}", user.email)
        return generateAuthResponse(user)
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        val accessToken = jwtTokenProvider.generateAccessToken(
            userId = user.id,
            email = user.email,
            role = user.role.name
        )
        val refreshTokenString = jwtTokenProvider.generateRefreshToken(user.id)

        val refreshToken = RefreshToken(
            user = user,
            token = refreshTokenString,
            expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpirationMs() / 1000
            )
        )
        refreshTokenRepository.save(refreshToken)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshTokenString,
            user = user.toDto()
        )
    }
}
