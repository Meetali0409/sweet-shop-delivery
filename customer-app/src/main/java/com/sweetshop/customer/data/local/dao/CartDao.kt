package com.sweetshop.customer.data.local.dao

import androidx.room.*
import com.sweetshop.customer.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND variantId = :variantId LIMIT 1")
    suspend fun getCartItem(productId: Long, variantId: Long?): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteCartItemById(itemId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>

    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getTotalItemCount(): Flow<Int?>
}
