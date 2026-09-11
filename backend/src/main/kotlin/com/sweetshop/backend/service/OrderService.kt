package com.sweetshop.backend.service

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.entity.Order
import com.sweetshop.backend.entity.OrderItem
import com.sweetshop.backend.entity.OrderStatus
import com.sweetshop.backend.entity.NotificationType
import com.sweetshop.backend.entity.PaymentStatus
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.OutOfStockException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.mapper.toListDto
import com.sweetshop.backend.repository.*
import com.sweetshop.backend.util.OrderNumberGenerator
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository,
    private val deliveryConfigRepository: DeliveryConfigRepository,
    private val serviceablePincodeRepository: ServiceablePincodeRepository,
    private val shopConfigRepository: ShopConfigRepository,
    private val couponService: CouponService,
    private val notificationService: NotificationService,
    private val orderNumberGenerator: OrderNumberGenerator
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    private val validTransitions = mapOf(
        OrderStatus.PLACED to setOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
        OrderStatus.CONFIRMED to setOf(OrderStatus.PREPARING, OrderStatus.CANCELLED),
        OrderStatus.PREPARING to setOf(OrderStatus.OUT_FOR_DELIVERY),
        OrderStatus.OUT_FOR_DELIVERY to setOf(OrderStatus.DELIVERED),
        OrderStatus.DELIVERED to emptySet(),
        OrderStatus.CANCELLED to emptySet()
    )

    @Transactional
    fun createOrder(userId: Long, request: CreateOrderRequest): OrderDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val cart = cartRepository.findByUserId(userId)
            ?: throw BadRequestException("Cart is empty")

        if (cart.items.isEmpty()) {
            throw BadRequestException("Cart is empty")
        }

        val address = addressRepository.findById(request.addressId)
            .orElseThrow { ResourceNotFoundException("Address not found with id: ${request.addressId}") }

        if (address.user.id != userId) {
            throw BadRequestException("Address does not belong to this user")
        }

        if (!serviceablePincodeRepository.existsByPincodeAndIsActiveTrue(address.pincode)) {
            throw BadRequestException("Delivery is not available for pincode: ${address.pincode}")
        }

        var subtotal = BigDecimal.ZERO
        val orderItems = mutableListOf<OrderItem>()

        for (cartItem in cart.items) {
            val product = productRepository.findByIdForUpdate(cartItem.product.id)
                ?: throw ResourceNotFoundException("Product not found: ${cartItem.product.name}")

            if (!product.isAvailable) {
                throw BadRequestException("Product '${product.name}' is no longer available")
            }

            if (product.stockQuantity < cartItem.quantity) {
                throw OutOfStockException(
                    "Insufficient stock for '${product.name}'. Available: ${product.stockQuantity}, Requested: ${cartItem.quantity}"
                )
            }

            val unitPrice = product.discountPrice ?: product.price
            val itemTotal = unitPrice.multiply(BigDecimal(cartItem.quantity))
            subtotal = subtotal.add(itemTotal)

            product.stockQuantity -= cartItem.quantity
            if (product.stockQuantity <= 0) {
                product.isAvailable = false
            }
            productRepository.save(product)

            orderItems.add(
                OrderItem(
                    order = Order(
                        orderNumber = "",
                        user = user,
                        addressId = address.id,
                        subtotal = BigDecimal.ZERO,
                        totalAmount = BigDecimal.ZERO
                    ),
                    product = product,
                    productName = product.name,
                    productImage = product.imageUrl,
                    quantity = cartItem.quantity,
                    selectedWeight = cartItem.selectedWeight,
                    unitPrice = unitPrice,
                    totalPrice = itemTotal
                )
            )
        }

        var discount = BigDecimal.ZERO
        if (!request.couponCode.isNullOrBlank()) {
            discount = couponService.applyCoupon(request.couponCode, subtotal)
        }

        val deliveryConfig = deliveryConfigRepository.findAll().firstOrNull()
        val deliveryCharge = if (deliveryConfig != null) {
            val threshold = deliveryConfig.freeDeliveryThreshold
            if (threshold != null && subtotal >= threshold) {
                BigDecimal.ZERO
            } else {
                deliveryConfig.deliveryCharge
            }
        } else {
            BigDecimal.ZERO
        }

        val taxableAmount = subtotal.subtract(discount)
        val taxRatePercent = shopConfigRepository.findAll().firstOrNull()?.taxRate ?: BigDecimal("5.00")
        val taxRate = taxRatePercent.divide(BigDecimal("100"), 4, RoundingMode.HALF_UP)
        val tax = taxableAmount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP)
        val totalAmount = taxableAmount.add(deliveryCharge).add(tax)

        val estimatedDays = deliveryConfig?.estimatedDeliveryDays ?: 3
        val estimatedDelivery = LocalDate.now().plusDays(estimatedDays.toLong())

        val orderNumber = orderNumberGenerator.generateOrderNumber()

        val paymentStatus = when (request.paymentMethod?.uppercase()) {
            "COD" -> PaymentStatus.COD
            else -> PaymentStatus.PENDING
        }

        val order = Order(
            orderNumber = orderNumber,
            user = user,
            addressId = address.id,
            subtotal = subtotal,
            discount = discount,
            deliveryCharge = deliveryCharge,
            tax = tax,
            totalAmount = totalAmount,
            paymentMethod = request.paymentMethod ?: "COD",
            paymentStatus = paymentStatus,
            orderStatus = OrderStatus.PLACED,
            couponCode = request.couponCode,
            notes = request.notes,
            estimatedDelivery = estimatedDelivery
        )

        orderItems.forEach { item ->
            item.order = order
            order.items.add(item)
        }

        val savedOrder = orderRepository.save(order)

        cart.items.clear()
        cartRepository.save(cart)

        notificationService.createNotification(
            userId = userId,
            title = "Order Placed",
            message = "Your order #${savedOrder.orderNumber} has been placed successfully!",
            type = NotificationType.ORDER_PLACED,
            referenceId = savedOrder.id
        )

        logger.info("Order created: orderNumber={}, userId={}, total={}", orderNumber, userId, totalAmount)
        return savedOrder.toDto(address)
    }

    fun getOrders(userId: Long, status: OrderStatus?, pageable: Pageable): PagedResponse<OrderListDto> {
        val page = if (status != null) {
            orderRepository.findByUserIdAndOrderStatus(userId, status, pageable)
        } else {
            orderRepository.findByUserId(userId, pageable)
        }

        return PagedResponse(
            content = page.content.map { it.toListDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    fun getOrderById(userId: Long, orderId: Long): OrderDto {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $orderId") }

        if (order.user.id != userId) {
            throw ResourceNotFoundException("Order not found with id: $orderId")
        }

        val address = addressRepository.findById(order.addressId).orElse(null)
        return order.toDto(address)
    }

    @Transactional
    fun cancelOrder(userId: Long, orderId: Long): OrderDto {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $orderId") }

        if (order.user.id != userId) {
            throw ResourceNotFoundException("Order not found with id: $orderId")
        }

        if (order.orderStatus != OrderStatus.PLACED && order.orderStatus != OrderStatus.CONFIRMED) {
            throw BadRequestException("Order can only be cancelled when status is PLACED or CONFIRMED. Current status: ${order.orderStatus}")
        }

        for (item in order.items) {
            val product = productRepository.findByIdForUpdate(item.product.id)
            if (product != null) {
                product.stockQuantity += item.quantity
                product.isAvailable = true
                productRepository.save(product)
            }
        }

        order.orderStatus = OrderStatus.CANCELLED
        val savedOrder = orderRepository.save(order)

        notificationService.createNotification(
            userId = userId,
            title = "Order Cancelled",
            message = "Your order #${order.orderNumber} has been cancelled.",
            type = NotificationType.ORDER_CANCELLED,
            referenceId = order.id
        )

        logger.info("Order cancelled: orderNumber={}, userId={}", order.orderNumber, userId)
        val address = addressRepository.findById(order.addressId).orElse(null)
        return savedOrder.toDto(address)
    }

    @Transactional
    fun reorder(userId: Long, orderId: Long): ReorderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $orderId") }

        if (order.user.id != userId) {
            throw ResourceNotFoundException("Order not found with id: $orderId")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        var cart = cartRepository.findByUserId(userId)
        if (cart == null) {
            cart = cartRepository.save(
                com.sweetshop.backend.entity.Cart(user = user)
            )
        }

        val addedItems = mutableListOf<String>()
        val unavailableItems = mutableListOf<String>()

        for (item in order.items) {
            val product = productRepository.findById(item.product.id).orElse(null)

            if (product == null || !product.isAvailable || product.stockQuantity < item.quantity) {
                unavailableItems.add(item.productName)
                continue
            }

            val existingItem = cart.items.find {
                it.product.id == product.id && it.selectedWeight == item.selectedWeight
            }

            val unitPrice = product.discountPrice ?: product.price

            if (existingItem != null) {
                existingItem.quantity += item.quantity
                existingItem.unitPrice = unitPrice
            } else {
                val cartItem = com.sweetshop.backend.entity.CartItem(
                    cart = cart,
                    product = product,
                    quantity = item.quantity,
                    selectedWeight = item.selectedWeight,
                    unitPrice = unitPrice
                )
                cart.items.add(cartItem)
            }
            addedItems.add(item.productName)
        }

        cartRepository.save(cart)

        val message = when {
            unavailableItems.isEmpty() -> "All items added to cart successfully"
            addedItems.isEmpty() -> "None of the items are available"
            else -> "${addedItems.size} items added, ${unavailableItems.size} items unavailable"
        }

        return ReorderResponse(
            addedItems = addedItems,
            unavailableItems = unavailableItems,
            message = message
        )
    }

    fun getAllOrders(status: OrderStatus?, pageable: Pageable): PagedResponse<OrderListDto> {
        val page = if (status != null) {
            orderRepository.findByOrderStatus(status, pageable)
        } else {
            orderRepository.findAll(pageable)
        }

        return PagedResponse(
            content = page.content.map { it.toListDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    fun getAdminOrderById(orderId: Long): OrderDto {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $orderId") }
        val address = addressRepository.findById(order.addressId).orElse(null)
        return order.toDto(address)
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, request: UpdateOrderStatusRequest): OrderDto {
        val order = orderRepository.findById(orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $orderId") }

        val allowedTransitions = validTransitions[order.orderStatus] ?: emptySet()
        if (request.status !in allowedTransitions) {
            throw BadRequestException(
                "Invalid status transition from ${order.orderStatus} to ${request.status}. " +
                "Allowed transitions: $allowedTransitions"
            )
        }

        if (request.status == OrderStatus.CANCELLED) {
            for (item in order.items) {
                val product = productRepository.findByIdForUpdate(item.product.id)
                if (product != null) {
                    product.stockQuantity += item.quantity
                    product.isAvailable = true
                    productRepository.save(product)
                }
            }
        }

        order.orderStatus = request.status
        val savedOrder = orderRepository.save(order)

        val notificationType = when (request.status) {
            OrderStatus.CONFIRMED -> NotificationType.ORDER_CONFIRMED
            OrderStatus.PREPARING -> NotificationType.ORDER_PREPARING
            OrderStatus.OUT_FOR_DELIVERY -> NotificationType.ORDER_OUT_FOR_DELIVERY
            OrderStatus.DELIVERED -> NotificationType.ORDER_DELIVERED
            OrderStatus.CANCELLED -> NotificationType.ORDER_CANCELLED
            else -> NotificationType.GENERAL
        }

        val statusMessage = when (request.status) {
            OrderStatus.CONFIRMED -> "Your order #${order.orderNumber} has been confirmed!"
            OrderStatus.PREPARING -> "Your order #${order.orderNumber} is being prepared."
            OrderStatus.OUT_FOR_DELIVERY -> "Your order #${order.orderNumber} is out for delivery!"
            OrderStatus.DELIVERED -> "Your order #${order.orderNumber} has been delivered!"
            OrderStatus.CANCELLED -> "Your order #${order.orderNumber} has been cancelled."
            else -> "Your order #${order.orderNumber} status updated to ${request.status}"
        }

        notificationService.createNotification(
            userId = order.user.id,
            title = "Order Update",
            message = statusMessage,
            type = notificationType,
            referenceId = order.id
        )

        logger.info("Order status updated: orderNumber={}, newStatus={}", order.orderNumber, request.status)
        val address = addressRepository.findById(order.addressId).orElse(null)
        return savedOrder.toDto(address)
    }
}
