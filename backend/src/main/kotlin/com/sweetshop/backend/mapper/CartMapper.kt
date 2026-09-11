package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.CartDto
import com.sweetshop.backend.dto.CartItemDto
import com.sweetshop.backend.entity.Cart
import com.sweetshop.backend.entity.CartItem
import java.math.BigDecimal

fun Cart.toDto(): CartDto {
    val cartItems = this.items.map { it.toDto() }
    val subtotal = cartItems.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.totalPrice) }
    return CartDto(
        items = cartItems,
        subtotal = subtotal,
        itemCount = cartItems.sumOf { it.quantity }
    )
}

fun CartItem.toDto(): CartItemDto = CartItemDto(
    id = this.id,
    productId = this.product.id,
    productName = this.product.name,
    productImage = this.product.imageUrl,
    quantity = this.quantity,
    selectedWeight = this.selectedWeight,
    unitPrice = this.unitPrice,
    totalPrice = this.unitPrice.multiply(BigDecimal(this.quantity)),
    isAvailable = this.product.isAvailable
)
