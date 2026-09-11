package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {

    fun findByProductId(productId: Long, pageable: Pageable): Page<Review>

    fun existsByUserIdAndProductIdAndOrderId(userId: Long, productId: Long, orderId: Long): Boolean

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.product.id = :productId")
    fun averageRatingByProductId(@Param("productId") productId: Long): Double

    fun countByProductId(productId: Long): Int
}
