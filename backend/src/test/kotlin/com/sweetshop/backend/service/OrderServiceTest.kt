package com.sweetshop.backend.service

import com.sweetshop.backend.dto.CreateOrderRequest
import com.sweetshop.backend.dto.UpdateOrderStatusRequest
import com.sweetshop.backend.entity.*
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.OutOfStockException
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
class OrderServiceTest {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var serviceablePincodeRepository: ServiceablePincodeRepository

    @Autowired
    private lateinit var deliveryConfigRepository: DeliveryConfigRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var customer: User
    private lateinit var product: Product
    private lateinit var address: Address

    @BeforeEach
    fun setup() {
        orderRepository.deleteAll()
        cartItemRepository.deleteAll()
        cartRepository.deleteAll()
        addressRepository.deleteAll()
        productRepository.deleteAll()
        categoryRepository.deleteAll()
        serviceablePincodeRepository.deleteAll()
        deliveryConfigRepository.deleteAll()
        userRepository.deleteAll()

        customer = userRepository.save(
            User(
                name = "Test Customer",
                email = "customer@test.com",
                phone = "9876543210",
                passwordHash = passwordEncoder.encode("Password@123"),
                role = UserRole.CUSTOMER
            )
        )

        val category = categoryRepository.save(
            Category(name = "Test Category", isActive = true, sortOrder = 1)
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

        address = addressRepository.save(
            Address(
                user = customer,
                name = "Test Address",
                phone = "9876543210",
                addressLine1 = "123 Test Street",
                city = "Mumbai",
                state = "Maharashtra",
                pincode = "400001",
                isDefault = true
            )
        )

        serviceablePincodeRepository.save(
            ServiceablePincode(pincode = "400001", city = "Mumbai", isActive = true)
        )

        deliveryConfigRepository.save(
            DeliveryConfig(
                deliveryCharge = BigDecimal("40.00"),
                freeDeliveryThreshold = BigDecimal("500.00"),
                estimatedDeliveryDays = 3
            )
        )

        // Add item to cart
        val cart = cartRepository.save(Cart(user = customer))
        cartItemRepository.save(
            CartItem(
                cart = cart,
                product = product,
                quantity = 2,
                selectedWeight = "500g",
                unitPrice = BigDecimal("240.00")
            )
        )
    }

    @Test
    fun `create order should succeed with valid data`() {
        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )

        val order = orderService.createOrder(customer.id!!, request)

        assertNotNull(order)
        assertTrue(order.orderNumber.startsWith("ORD-"))
        assertEquals("PLACED", order.orderStatus)
        assertEquals(BigDecimal("480.00").setScale(2), order.subtotal.setScale(2))

        // Verify stock decreased
        val updatedProduct = productRepository.findById(product.id!!).get()
        assertEquals(8, updatedProduct.stockQuantity)

        // Verify cart cleared
        val cart = cartRepository.findByUserId(customer.id!!)
        assertTrue(cart == null || cart.items.isEmpty())
    }

    @Test
    fun `create order should fail when stock insufficient`() {
        // Set stock to 1 but cart has quantity 2
        product.stockQuantity = 1
        productRepository.save(product)

        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )

        assertThrows<OutOfStockException> {
            orderService.createOrder(customer.id!!, request)
        }
    }

    @Test
    fun `cancel order should restore stock`() {
        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )
        val order = orderService.createOrder(customer.id!!, request)

        // Stock should be 8 after order
        assertEquals(8, productRepository.findById(product.id!!).get().stockQuantity)

        // Cancel the order
        orderService.cancelOrder(customer.id!!, order.id)

        // Stock should be restored to 10
        assertEquals(10, productRepository.findById(product.id!!).get().stockQuantity)
    }

    @Test
    fun `cancel delivered order should fail`() {
        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )
        val order = orderService.createOrder(customer.id!!, request)

        // Admin updates status to DELIVERED
        val orderEntity = orderRepository.findById(order.id).get()
        orderEntity.orderStatus = OrderStatus.DELIVERED
        orderRepository.save(orderEntity)

        assertThrows<BadRequestException> {
            orderService.cancelOrder(customer.id!!, order.id)
        }
    }

    @Test
    fun `invalid status transition should fail`() {
        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )
        val order = orderService.createOrder(customer.id!!, request)

        // Try to jump from PLACED to DELIVERED (invalid)
        assertThrows<BadRequestException> {
            orderService.updateOrderStatus(
                order.id,
                UpdateOrderStatusRequest(status = OrderStatus.DELIVERED)
            )
        }
    }

    @Test
    fun `valid status transition should succeed`() {
        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )
        val order = orderService.createOrder(customer.id!!, request)

        // PLACED -> CONFIRMED (valid)
        val updated = orderService.updateOrderStatus(
            order.id,
            UpdateOrderStatusRequest(status = OrderStatus.CONFIRMED)
        )
        assertEquals("CONFIRMED", updated.orderStatus)
    }

    @Test
    fun `stock cannot become negative`() {
        product.stockQuantity = 0
        productRepository.save(product)

        val request = CreateOrderRequest(
            addressId = address.id!!,
            paymentMethod = "COD"
        )

        assertThrows<OutOfStockException> {
            orderService.createOrder(customer.id!!, request)
        }

        // Verify stock is still 0
        assertEquals(0, productRepository.findById(product.id!!).get().stockQuantity)
    }
}
