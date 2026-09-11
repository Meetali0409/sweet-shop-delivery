package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.Cart
import com.sweetshop.customer.util.Resource

interface CartRepository {
    suspend fun getCart(): Resource<Cart>
    suspend fun addToCart(productId: Long, selectedWeight: String?, quantity: Int): Resource<Cart>
    suspend fun updateCartItem(itemId: Long, quantity: Int): Resource<Cart>
    suspend fun removeCartItem(itemId: Long): Resource<Cart>
    suspend fun clearCart(): Resource<Unit>
    suspend fun applyCoupon(couponCode: String): Resource<Cart>
    suspend fun removeCoupon(): Resource<Cart>
    suspend fun getCartItemCount(): Resource<Int>
}
