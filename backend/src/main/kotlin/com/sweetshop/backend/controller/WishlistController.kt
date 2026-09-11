package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.ApiResponse
import com.sweetshop.backend.dto.WishlistItemDto
import com.sweetshop.backend.security.UserPrincipal
import com.sweetshop.backend.service.WishlistService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/wishlist")
class WishlistController(
    private val wishlistService: WishlistService
) {

    @GetMapping
    fun getWishlist(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ApiResponse<List<WishlistItemDto>>> {
        val wishlist = wishlistService.getWishlist(userPrincipal.id)
        return ResponseEntity.ok(ApiResponse(success = true, data = wishlist))
    }

    @PostMapping("/{productId}")
    fun addToWishlist(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<ApiResponse<WishlistItemDto>> {
        val item = wishlistService.addToWishlist(userPrincipal.id, productId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = item, message = "Added to wishlist")
        )
    }

    @DeleteMapping("/{productId}")
    fun removeFromWishlist(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        wishlistService.removeFromWishlist(userPrincipal.id, productId)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = null, message = "Removed from wishlist")
        )
    }
}
