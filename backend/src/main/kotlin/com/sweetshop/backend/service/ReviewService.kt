package com.sweetshop.backend.service

import com.sweetshop.backend.dto.CreateReviewRequest
import com.sweetshop.backend.dto.PagedResponse
import com.sweetshop.backend.dto.ReviewDto
import com.sweetshop.backend.entity.OrderStatus
import com.sweetshop.backend.entity.Review
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.ConflictException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.OrderRepository
import com.sweetshop.backend.repository.ProductRepository
import com.sweetshop.backend.repository.ReviewRepository
import com.sweetshop.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) {
    private val logger = LoggerFactory.getLogger(ReviewService::class.java)

    fun getProductReviews(productId: Long, pageable: Pageable): PagedResponse<ReviewDto> {
        if (!productRepository.existsById(productId)) {
            throw ResourceNotFoundException("Product not found with id: $productId")
        }

        val page = reviewRepository.findByProductId(productId, pageable)
        return PagedResponse(
            content = page.content.map { it.toDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    @Transactional
    fun createReview(userId: Long, productId: Long, request: CreateReviewRequest): ReviewDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val product = productRepository.findById(productId)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $productId") }

        val order = orderRepository.findById(request.orderId)
            .orElseThrow { ResourceNotFoundException("Order not found with id: ${request.orderId}") }

        if (order.user.id != userId) {
            throw BadRequestException("This order does not belong to you")
        }

        if (order.orderStatus != OrderStatus.DELIVERED) {
            throw BadRequestException("You can only review products from delivered orders")
        }

        val hasOrderedProduct = order.items.any { it.product.id == productId }
        if (!hasOrderedProduct) {
            throw BadRequestException("This product was not part of the specified order")
        }

        if (reviewRepository.existsByUserIdAndProductIdAndOrderId(userId, productId, request.orderId)) {
            throw ConflictException("You have already reviewed this product for this order")
        }

        val review = Review(
            user = user,
            product = product,
            orderId = request.orderId,
            rating = request.rating,
            review = request.review
        )

        val savedReview = reviewRepository.save(review)

        val avgRating = reviewRepository.averageRatingByProductId(productId)
        val totalReviews = reviewRepository.countByProductId(productId)
        product.rating = BigDecimal(avgRating).setScale(2, RoundingMode.HALF_UP)
        product.totalReviews = totalReviews
        productRepository.save(product)

        logger.info("Review created: userId={}, productId={}, rating={}", userId, productId, request.rating)
        return savedReview.toDto()
    }
}
