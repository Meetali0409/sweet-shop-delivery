package com.sweetshop.backend.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CartDto(
    val items: List<CartItemDto>,
    val subtotal: BigDecimal,
    val itemCount: Int
)

data class CartItemDto(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productImage: String?,
    val quantity: Int,
    val selectedWeight: String?,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val isAvailable: Boolean
)

data class AddToCartRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int = 1,

    val selectedWeight: String? = null
)

data class UpdateCartItemRequest(
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)
