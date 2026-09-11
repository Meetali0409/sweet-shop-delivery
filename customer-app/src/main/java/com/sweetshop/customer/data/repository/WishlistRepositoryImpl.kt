package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.repository.WishlistRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepositoryImpl @Inject constructor(
    private val api: SweetShopApi
) : WishlistRepository {

    override suspend fun getWishlist(): Resource<List<Product>> {
        return try {
            val response = api.getWishlist()
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data!!.map { it.toDomain() }
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load wishlist")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun addToWishlist(productId: Long): Resource<Unit> {
        return try {
            val response = api.addToWishlist(productId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to add to wishlist")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun removeFromWishlist(productId: Long): Resource<Unit> {
        return try {
            val response = api.removeFromWishlist(productId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to remove from wishlist")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun isInWishlist(productId: Long): Resource<Boolean> {
        return try {
            val response = api.getWishlist()
            if (response.isSuccessful && response.body()?.success == true) {
                val isInWishlist = response.body()!!.data!!.any { it.id == productId }
                Resource.Success(isInWishlist)
            } else {
                Resource.Success(false)
            }
        } catch (e: Exception) {
            Resource.Success(false)
        }
    }
}
