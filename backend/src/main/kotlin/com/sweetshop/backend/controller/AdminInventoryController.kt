package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/inventory")
@PreAuthorize("hasRole('ADMIN')")
class AdminInventoryController(
    private val productService: ProductService
) {

    @GetMapping
    fun getInventory(
        @RequestParam(defaultValue = "10") lowStockThreshold: Int
    ): ResponseEntity<ApiResponse<List<InventoryItemDto>>> {
        val items = productService.getAllInventory(lowStockThreshold)
        return ResponseEntity.ok(ApiResponse(success = true, data = items))
    }

    @PutMapping("/{productId}/stock")
    fun updateStock(
        @PathVariable productId: Long,
        @RequestBody body: Map<String, Int>
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val quantity = body["quantity"] ?: throw BadRequestException("Quantity is required")
        val product = productService.updateStock(productId, quantity)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = product, message = "Stock updated successfully")
        )
    }
}
