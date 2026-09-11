package com.sweetshop.backend.service

import com.sweetshop.backend.dto.CreateReviewRequest
import com.sweetshop.backend.entity.*
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.repository.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private lateinit var reviewService: ReviewService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var customer: User
    private lateinit var product: Product
    private lateinit var deliveredOrder: Order
    private lateinit var placedOrder: Order

    @BeforeEach
    fun setup() {
        reviewRepository.deleteAll()
        orderItemRepository.deleteAll()
        orderRepository.deleteAll()
        productRepository.deleteAll()
        categoryRepository.deleteAll()
        userRepository.deleteAll()

        customer = userRepository.save(
            User(
                name = "Customer",
                email = "customer@test.com",
                phone = "9876543210",
                passwordHash = passwordEncoder.encode("Password@123"),
                role = UserRole.CUSTOMER
            )
        )

        val category = categoryRepository.save(
            Category(name = "Test", isActive = true, sortOrder = 1)
        )

        product = productRepository.save(
            Product(
                name = "Test Sweet",
                category = category,
                price = BigDecimal("240.00"),
                unit = "500g",
                stockQuantity = 10,
                isAvailable = true
            )
        )

        deliveredOrder = orderRepository.save(
            Order(
                orderNumber = "ORD-TEST1",
                user = customer,
                addressId = 1L,
                subtotal = BigDecimal("240.00"),
                totalAmount = BigDecimal("280.00"),
                deliveryCharge = BigDecimal("40.00"),
                orderStatus = OrderStatus.DELIVERED,
                paymentStatus = PaymentStatus.COD
            )
        )

        orderItemRepository.save(
            OrderItem(
                order = deliveredOrder,
                product = product,
                productName = "Test Sweet",
                quantity = 1,
                selectedWeight = "500g",
                unitPrice = BigDecimal("240.00"),
                totalPrice = BigDecimal("240.00")
            )
        )

        placedOrder = orderRepository.save(
            Order(
                orderNumber = "ORD-TEST2",
                user = customer,
                addressId = 1L,
                subtotal = BigDecimal("240.00"),
                totalAmount = BigDecimal("280.00"),
                deliveryCharge = BigDecimal("40.00"),
                orderStatus = OrderStatus.PLACED,
                paymentStatus = PaymentStatus.COD
            )
        )

        orderItemRepository.save(
            OrderItem(
                order = placedOrder,
                product = product,
                productName = "Test Sweet",
                quantity = 1,
                selectedWeight = "500g",
                unitPrice = BigDecimal("240.00"),
                totalPrice = BigDecimal("240.00")
            )
        )
    }

    @Test
    fun `create review for delivered order should succeed`() {
        val request = CreateReviewRequest(
            orderId = deliveredOrder.id!!,
            rating = 5,
            review = "Excellent quality sweets!"
        )

        val review = reviewService.createReview(customer.id!!, product.id!!, request)

        assertEquals(5, review.rating)
        assertEquals("Excellent quality sweets!", review.review)
    }

    @Test
    fun `create review for undelivered order should fail`() {
        val request = CreateReviewRequest(
            orderId = placedOrder.id!!,
            rating = 4,
            review = "Good"
        )

        assertThrows<BadRequestException> {
            reviewService.createReview(customer.id!!, product.id!!, request)
        }
    }

    @Test
    fun `duplicate review should fail`() {
        val request = CreateReviewRequest(
            orderId = deliveredOrder.id!!,
            rating = 5,
            review = "Great!"
        )

        reviewService.createReview(customer.id!!, product.id!!, request)

        assertThrows<BadRequestException> {
            reviewService.createReview(customer.id!!, product.id!!, request)
        }
    }

    @Test
    fun `review should update product rating`() {
        val request = CreateReviewRequest(
            orderId = deliveredOrder.id!!,
            rating = 4,
            review = "Good"
        )

        reviewService.createReview(customer.id!!, product.id!!, request)

        val updatedProduct = productRepository.findById(product.id!!).get()
        assertTrue(updatedProduct.totalReviews > 0)
    }

    @Test
    fun `review from non-purchaser should fail`() {
        val otherCustomer = userRepository.save(
            User(
                name = "Other",
                email = "other@test.com",
                phone = "9876543211",
                passwordHash = passwordEncoder.encode("Password@123"),
                role = UserRole.CUSTOMER
            )
        )

        val request = CreateReviewRequest(
            orderId = deliveredOrder.id!!,
            rating = 3,
            review = "Shouldn't work"
        )

        assertThrows<BadRequestException> {
            reviewService.createReview(otherCustomer.id!!, product.id!!, request)
        }
    }
}
