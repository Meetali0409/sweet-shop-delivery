package com.sweetshop.backend.service

import com.sweetshop.backend.dto.WishlistItemDto
import com.sweetshop.backend.entity.WishlistItem
import com.sweetshop.backend.exception.ConflictException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.ProductRepository
import com.sweetshop.backend.repository.UserRepository
import com.sweetshop.backend.repository.WishlistItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val wishlistItemRepository: WishlistItemRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(WishlistService::class.java)

    fun getWishlist(userId: Long): List<WishlistItemDto> {
        return wishlistItemRepository.findByUserId(userId).map { it.toDto() }
    }

    @Transactional
    fun addToWishlist(userId: Long, productId: Long): WishlistItemDto {
        if (wishlistItemRepository.existsByUserIdAndProductId(userId, productId)) {
            throw ConflictException("Product already in wishlist")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val product = productRepository.findById(productId)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $productId") }

        val wishlistItem = WishlistItem(
            user = user,
            product = product
        )

        val savedItem = wishlistItemRepository.save(wishlistItem)
        logger.info("Product added to wishlist: userId={}, productId={}", userId, productId)
        return savedItem.toDto()
    }

    @Transactional
    fun removeFromWishlist(userId: Long, productId: Long) {
        if (!wishlistItemRepository.existsByUserIdAndProductId(userId, productId)) {
            throw ResourceNotFoundException("Product not found in wishlist")
        }
        wishlistItemRepository.deleteByUserIdAndProductId(userId, productId)
        logger.info("Product removed from wishlist: userId={}, productId={}", userId, productId)
    }
}
