package com.sweetshop.customer.domain.model

data class Address(
    val id: Long = 0,
    val label: String = "",
    val fullName: String = "",
    val phone: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    val isDefault: Boolean = false,
    val type: String = "HOME"
) {
    fun formattedAddress(): String {
        return buildString {
            append(addressLine1)
            if (addressLine2.isNotBlank()) append(", $addressLine2")
            if (landmark.isNotBlank()) append(", Near $landmark")
            append(", $city, $state - $pincode")
        }
    }
}
