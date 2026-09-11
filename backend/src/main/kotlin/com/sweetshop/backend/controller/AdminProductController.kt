package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
class AdminProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productService.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = product, message = "Product created successfully")
        )
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = product, message = "Product updated successfully")
        )
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        productService.deleteProduct(id)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = null, message = "Product deleted successfully")
        )
    }

    @PutMapping("/{id}/stock")
    fun updateStock(
        @PathVariable id: Long,
        @RequestBody body: Map<String, Int>
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val quantity = body["quantity"] ?: throw com.sweetshop.backend.exception.BadRequestException("Quantity is required")
        val product = productService.updateStock(id, quantity)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = product, message = "Stock updated successfully")
        )
    }
}
