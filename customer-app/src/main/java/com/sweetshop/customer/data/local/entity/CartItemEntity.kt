package com.sweetshop.customer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sweetshop.customer.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val productImageUrl: String? = null,
    val variantId: Long? = null,
    val weight: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val discountedPrice: Double? = null
) {
    fun toDomain(): CartItem = CartItem(
        id = id,
        productId = productId,
        productName = productName,
        productImageUrl = productImageUrl,
        variantId = variantId,
        weight = weight,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = unitPrice * quantity,
        discountedPrice = discountedPrice
    )
}
