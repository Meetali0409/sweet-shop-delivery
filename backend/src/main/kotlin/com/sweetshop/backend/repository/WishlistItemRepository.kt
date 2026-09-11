package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.WishlistItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface WishlistItemRepository : JpaRepository<WishlistItem, Long> {
    fun findByUserId(userId: Long): List<WishlistItem>
    fun findByUserIdAndProductId(userId: Long, productId: Long): WishlistItem?
    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    @Transactional
    fun deleteByUserIdAndProductId(userId: Long, productId: Long)
}
