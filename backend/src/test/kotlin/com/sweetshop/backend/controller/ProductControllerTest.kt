package com.sweetshop.backend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.sweetshop.backend.entity.*
import com.sweetshop.backend.repository.CategoryRepository
import com.sweetshop.backend.repository.ProductRepository
import com.sweetshop.backend.repository.UserRepository
import com.sweetshop.backend.security.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var category: Category
    private lateinit var adminToken: String

    @BeforeEach
    fun setup() {
        productRepository.deleteAll()
        categoryRepository.deleteAll()
        userRepository.deleteAll()

        category = categoryRepository.save(
            Category(
                name = "Traditional",
                description = "Traditional sweets",
                isActive = true,
                sortOrder = 1
            )
        )

        val adminUser = userRepository.save(
            User(
                name = "Admin",
                email = "admin@test.com",
                phone = "9876543210",
                passwordHash = passwordEncoder.encode("Admin@123"),
                role = UserRole.ADMIN
            )
        )
        adminToken = jwtTokenProvider.generateAccessToken(adminUser.id!!, adminUser.email, adminUser.role.name)

        productRepository.save(
            Product(
                name = "Gulab Jamun",
                description = "Delicious sweet",
                category = category,
                price = BigDecimal("240.00"),
                unit = "500g",
                stockQuantity = 100,
                isAvailable = true,
                isFeatured = true,
                isBestseller = true,
                rating = BigDecimal("4.5"),
                totalReviews = 50
            )
        )

        productRepository.save(
            Product(
                name = "Kaju Katli",
                description = "Premium cashew fudge",
                category = category,
                price = BigDecimal("600.00"),
                discountPrice = BigDecimal("560.00"),
                unit = "500g",
                stockQuantity = 80,
                isAvailable = true,
                isFeatured = true,
                isBestseller = true,
                rating = BigDecimal("4.7"),
                totalReviews = 95
            )
        )
    }

    @Test
    fun `get products should return paginated list`() {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.data.totalElements").value(2))
    }

    @Test
    fun `get product by id should return product details`() {
        val products = productRepository.findAll()
        val productId = products.first().id!!

        mockMvc.perform(get("/api/v1/products/$productId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Gulab Jamun"))
    }

    @Test
    fun `get featured products should return only featured`() {
        mockMvc.perform(get("/api/v1/products/featured"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `get bestseller products should return only bestsellers`() {
        mockMvc.perform(get("/api/v1/products/bestsellers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray)
    }

    @Test
    fun `search products should filter by name`() {
        mockMvc.perform(get("/api/v1/products?search=gulab"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].name").value("Gulab Jamun"))
    }

    @Test
    fun `get products by category should filter correctly`() {
        mockMvc.perform(get("/api/v1/products?category=${category.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalElements").value(2))
    }

    @Test
    fun `get nonexistent product should return 404`() {
        mockMvc.perform(get("/api/v1/products/99999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `customer cannot access admin product endpoints`() {
        val customerUser = userRepository.save(
            User(
                name = "Customer",
                email = "customer@test.com",
                phone = "9876543211",
                passwordHash = passwordEncoder.encode("Customer@123"),
                role = UserRole.CUSTOMER
            )
        )
        val customerToken = jwtTokenProvider.generateAccessToken(customerUser.id!!, customerUser.email, customerUser.role.name)

        mockMvc.perform(
            get("/api/v1/admin/orders")
                .header("Authorization", "Bearer $customerToken")
        )
            .andExpect(status().isForbidden)
    }
}
