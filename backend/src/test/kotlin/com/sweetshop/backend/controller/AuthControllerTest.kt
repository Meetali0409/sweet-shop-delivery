package com.sweetshop.backend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.sweetshop.backend.dto.LoginRequest
import com.sweetshop.backend.dto.RegisterRequest
import com.sweetshop.backend.entity.User
import com.sweetshop.backend.entity.UserRole
import com.sweetshop.backend.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `register should create new user and return tokens`() {
        val request = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            phone = "9876543210",
            password = "Password@123",
            confirmPassword = "Password@123"
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty)
            .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.user.role").value("CUSTOMER"))
    }

    @Test
    fun `register should fail with duplicate email`() {
        val user = User(
            name = "Existing User",
            email = "test@example.com",
            phone = "9876543210",
            passwordHash = passwordEncoder.encode("Password@123"),
            role = UserRole.CUSTOMER
        )
        userRepository.save(user)

        val request = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            phone = "9876543211",
            password = "Password@123",
            confirmPassword = "Password@123"
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `login should return tokens for valid credentials`() {
        val user = User(
            name = "Test User",
            email = "test@example.com",
            phone = "9876543210",
            passwordHash = passwordEncoder.encode("Password@123"),
            role = UserRole.CUSTOMER
        )
        userRepository.save(user)

        val request = LoginRequest(
            email = "test@example.com",
            password = "Password@123"
        )

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
    }

    @Test
    fun `login should fail with invalid credentials`() {
        val request = LoginRequest(
            email = "nonexistent@example.com",
            password = "WrongPassword"
        )

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `register should fail with mismatched passwords`() {
        val request = RegisterRequest(
            name = "Test User",
            email = "test@example.com",
            phone = "9876543210",
            password = "Password@123",
            confirmPassword = "DifferentPassword"
        )

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }
}
