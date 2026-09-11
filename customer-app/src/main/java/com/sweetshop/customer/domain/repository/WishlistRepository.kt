package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.util.Resource

interface WishlistRepository {
    suspend fun getWishlist(): Resource<List<Product>>
    suspend fun addToWishlist(productId: Long): Resource<Unit>
    suspend fun removeFromWishlist(productId: Long): Resource<Unit>
    suspend fun isInWishlist(productId: Long): Resource<Boolean>
}
