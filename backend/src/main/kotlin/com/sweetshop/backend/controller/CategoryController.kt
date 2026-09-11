package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.ApiResponse
import com.sweetshop.backend.dto.CategoryDto
import com.sweetshop.backend.service.CategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun getCategories(): ResponseEntity<ApiResponse<List<CategoryDto>>> {
        val categories = categoryService.getCategories()
        return ResponseEntity.ok(ApiResponse(success = true, data = categories))
    }
}
