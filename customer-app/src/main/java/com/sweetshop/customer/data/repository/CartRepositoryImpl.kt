package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.data.dto.AddToCartRequest
import com.sweetshop.customer.data.dto.ApplyCouponRequest
import com.sweetshop.customer.data.dto.UpdateCartItemRequest
import com.sweetshop.customer.domain.model.Cart
import com.sweetshop.customer.domain.repository.CartRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val api: SweetShopApi
) : CartRepository {

    override suspend fun getCart(): Resource<Cart> {
        return try {
            val response = api.getCart()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load cart")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun addToCart(productId: Long, selectedWeight: String?, quantity: Int): Resource<Cart> {
        return try {
            val response = api.addToCart(AddToCartRequest(productId, selectedWeight, quantity))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to add to cart")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateCartItem(itemId: Long, quantity: Int): Resource<Cart> {
        return try {
            val response = api.updateCartItem(itemId, UpdateCartItemRequest(quantity))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to update cart")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun removeCartItem(itemId: Long): Resource<Cart> {
        return try {
            val response = api.removeCartItem(itemId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to remove item")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun clearCart(): Resource<Unit> {
        return try {
            val response = api.clearCart()
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to clear cart")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun applyCoupon(couponCode: String): Resource<Cart> {
        return try {
            val response = api.applyCoupon(ApplyCouponRequest(couponCode))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Invalid coupon code")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun removeCoupon(): Resource<Cart> {
        return try {
            val response = api.removeCoupon()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to remove coupon")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getCartItemCount(): Resource<Int> {
        return try {
            val response = api.getCart()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.itemCount)
            } else {
                Resource.Success(0)
            }
        } catch (e: Exception) {
            Resource.Success(0)
        }
    }
}
