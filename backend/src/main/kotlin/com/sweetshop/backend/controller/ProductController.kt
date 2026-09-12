package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.security.UserPrincipal
import com.sweetshop.backend.service.ProductService
import com.sweetshop.backend.service.ReviewService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService,
    private val reviewService: ReviewService
) {

    @GetMapping
    fun getProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) category: Long?,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "createdAt") sort: String?
    ): ResponseEntity<ApiResponse<PagedResponse<ProductListDto>>> {
        val sortValue = sort ?: "createdAt"
        val parts = sortValue.split(",")
        val property = parts[0].trim()
        val direction = if (parts.size > 1 && parts[1].trim().equals("asc", ignoreCase = true))
            Sort.Direction.ASC else Sort.Direction.DESC
        val allowedFields = setOf("createdAt", "price", "rating", "name", "totalReviews")
        val safeProperty = if (property in allowedFields) property else "createdAt"
        val pageable = PageRequest.of(page, size, Sort.by(direction, safeProperty))
        val response = productService.getProducts(category, search, sort, pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val response = productService.getProductById(id, userPrincipal?.id)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }

    @GetMapping("/featured")
    fun getFeaturedProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<ProductListDto>>> {
        val pageable = PageRequest.of(page, size)
        val response = productService.getFeaturedProducts(pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }

    @GetMapping("/bestsellers")
    fun getBestsellerProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<ProductListDto>>> {
        val pageable = PageRequest.of(page, size)
        val response = productService.getBestsellerProducts(pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }

    @GetMapping("/{id}/reviews")
    fun getProductReviews(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<ReviewDto>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val response = reviewService.getProductReviews(id, pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = response))
    }

    @PostMapping("/{id}/reviews")
    fun createReview(
        @PathVariable id: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: CreateReviewRequest
    ): ResponseEntity<ApiResponse<ReviewDto>> {
        val response = reviewService.createReview(userPrincipal.id, id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = response, message = "Review submitted successfully")
        )
    }
}
