package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.security.UserPrincipal
import com.sweetshop.backend.service.CartService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/cart")
class CartController(
    private val cartService: CartService
) {

    @GetMapping
    fun getCart(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ApiResponse<CartDto>> {
        val cart = cartService.getCart(userPrincipal.id)
        return ResponseEntity.ok(ApiResponse(success = true, data = cart))
    }

    @PostMapping("/items")
    fun addToCart(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: AddToCartRequest
    ): ResponseEntity<ApiResponse<CartDto>> {
        val cart = cartService.addToCart(userPrincipal.id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = cart, message = "Item added to cart")
        )
    }

    @PutMapping("/items/{id}")
    fun updateCartItem(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCartItemRequest
    ): ResponseEntity<ApiResponse<CartDto>> {
        val cart = cartService.updateCartItem(userPrincipal.id, id, request)
        return ResponseEntity.ok(ApiResponse(success = true, data = cart, message = "Cart updated"))
    }

    @DeleteMapping("/items/{id}")
    fun removeCartItem(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<CartDto>> {
        val cart = cartService.removeCartItem(userPrincipal.id, id)
        return ResponseEntity.ok(ApiResponse(success = true, data = cart, message = "Item removed from cart"))
    }

    @DeleteMapping
    fun clearCart(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ApiResponse<Nothing>> {
        cartService.clearCart(userPrincipal.id)
        return ResponseEntity.ok(ApiResponse(success = true, data = null, message = "Cart cleared"))
    }
}
