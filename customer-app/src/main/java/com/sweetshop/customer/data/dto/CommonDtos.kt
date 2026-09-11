package com.sweetshop.customer.data.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("errorCode") val errorCode: String? = null
)

data class PagedResponse<T>(
    @SerializedName("content") val content: List<T> = emptyList(),
    @SerializedName("totalElements") val totalElements: Long = 0,
    @SerializedName("totalPages") val totalPages: Int = 0,
    @SerializedName("page") val page: Int = 0,
    @SerializedName("size") val size: Int = 20,
    @SerializedName("hasNext") val hasNext: Boolean = false
)
