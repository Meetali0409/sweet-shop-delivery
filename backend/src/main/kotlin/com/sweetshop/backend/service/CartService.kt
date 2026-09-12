package com.sweetshop.backend.service

import com.sweetshop.backend.dto.AddToCartRequest
import com.sweetshop.backend.dto.CartDto
import com.sweetshop.backend.dto.UpdateCartItemRequest
import com.sweetshop.backend.entity.Cart
import com.sweetshop.backend.entity.CartItem
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.OutOfStockException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.CartItemRepository
import com.sweetshop.backend.repository.CartRepository
import com.sweetshop.backend.repository.ProductRepository
import com.sweetshop.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(CartService::class.java)

    fun getCart(userId: Long): CartDto {
        val cart = cartRepository.findByUserId(userId)
            ?: return CartDto(
                items = emptyList(),
                subtotal = java.math.BigDecimal.ZERO,
                total = java.math.BigDecimal.ZERO,
                itemCount = 0
            )
        return cart.toDto()
    }

    @Transactional
    fun addToCart(userId: Long, request: AddToCartRequest): CartDto {
        val product = productRepository.findById(request.productId)
            .orElseThrow { ResourceNotFoundException("Product not found with id: ${request.productId}") }

        if (!product.isAvailable) {
            throw BadRequestException("Product '${product.name}' is currently unavailable")
        }

        if (product.stockQuantity < request.quantity) {
            throw OutOfStockException("Insufficient stock for '${product.name}'. Available: ${product.stockQuantity}")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        var cart = cartRepository.findByUserId(userId)
        if (cart == null) {
            cart = Cart(user = user)
            cart = cartRepository.save(cart)
        }

        val existingItem = cart.items.find {
            it.product.id == request.productId && it.selectedWeight == request.selectedWeight
        }

        val unitPrice = product.discountPrice ?: product.price

        if (existingItem != null) {
            existingItem.quantity += request.quantity
            existingItem.unitPrice = unitPrice
        } else {
            val cartItem = CartItem(
                cart = cart,
                product = product,
                quantity = request.quantity,
                selectedWeight = request.selectedWeight,
                unitPrice = unitPrice
            )
            cart.items.add(cartItem)
        }

        val savedCart = cartRepository.save(cart)
        logger.info("Item added to cart: userId={}, productId={}, qty={}", userId, request.productId, request.quantity)
        return savedCart.toDto()
    }

    @Transactional
    fun updateCartItem(userId: Long, itemId: Long, request: UpdateCartItemRequest): CartDto {
        val cart = cartRepository.findByUserId(userId)
            ?: throw ResourceNotFoundException("Cart not found")

        val item = cart.items.find { it.id == itemId }
            ?: throw ResourceNotFoundException("Cart item not found with id: $itemId")

        if (item.product.stockQuantity < request.quantity) {
            throw OutOfStockException(
                "Insufficient stock for '${item.product.name}'. Available: ${item.product.stockQuantity}"
            )
        }

        item.quantity = request.quantity
        val savedCart = cartRepository.save(cart)
        logger.info("Cart item updated: userId={}, itemId={}, qty={}", userId, itemId, request.quantity)
        return savedCart.toDto()
    }

    @Transactional
    fun removeCartItem(userId: Long, itemId: Long): CartDto {
        val cart = cartRepository.findByUserId(userId)
            ?: throw ResourceNotFoundException("Cart not found")

        val removed = cart.items.removeIf { it.id == itemId }
        if (!removed) {
            throw ResourceNotFoundException("Cart item not found with id: $itemId")
        }

        val savedCart = cartRepository.save(cart)
        logger.info("Cart item removed: userId={}, itemId={}", userId, itemId)
        return savedCart.toDto()
    }

    @Transactional
    fun clearCart(userId: Long) {
        val cart = cartRepository.findByUserId(userId) ?: return
        cart.items.clear()
        cartRepository.save(cart)
        logger.info("Cart cleared: userId={}", userId)
    }
}
