package com.sweetshop.admin.data.dto

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
    val code: String?
)

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
)
