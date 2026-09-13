package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
class AdminCategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<ApiResponse<List<CategoryDto>>> {
        val categories = categoryService.getAllCategories()
        return ResponseEntity.ok(ApiResponse(success = true, data = categories))
    }

    @PostMapping
    fun createCategory(
        @Valid @RequestBody request: CreateCategoryRequest
    ): ResponseEntity<ApiResponse<CategoryDto>> {
        val category = categoryService.createCategory(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = category, message = "Category created successfully")
        )
    }

    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<ApiResponse<CategoryDto>> {
        val category = categoryService.updateCategory(id, request)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = category, message = "Category updated successfully")
        )
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        categoryService.deleteCategory(id)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = null, message = "Category deleted successfully")
        )
    }
}
