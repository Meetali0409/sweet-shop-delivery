package com.sweetshop.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class AddressDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val phone: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val pincode: String,
    val landmark: String?,
    val isDefault: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CreateAddressRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Phone is required")
    @field:Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    val phone: String,

    @field:NotBlank(message = "Address line 1 is required")
    val addressLine1: String,

    val addressLine2: String? = null,

    @field:NotBlank(message = "City is required")
    val city: String,

    @field:NotBlank(message = "State is required")
    val state: String,

    @field:NotBlank(message = "Pincode is required")
    @field:Size(min = 6, max = 6, message = "Pincode must be 6 digits")
    val pincode: String,

    val landmark: String? = null,
    val isDefault: Boolean = false
)

data class UpdateAddressRequest(
    val name: String? = null,
    val phone: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val landmark: String? = null,
    val isDefault: Boolean? = null
)
