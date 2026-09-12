package com.sweetshop.customer.data.dto

import com.google.gson.annotations.SerializedName
import com.sweetshop.customer.domain.model.Address

data class CreateAddressRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("addressLine1") val addressLine1: String,
    @SerializedName("addressLine2") val addressLine2: String = "",
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("landmark") val landmark: String = "",
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("addressType") val addressType: String = "HOME",
    @SerializedName("isDefault") val isDefault: Boolean = false
)

data class AddressDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("addressLine1") val addressLine1: String = "",
    @SerializedName("addressLine2") val addressLine2: String = "",
    @SerializedName("city") val city: String = "",
    @SerializedName("state") val state: String = "",
    @SerializedName("pincode") val pincode: String = "",
    @SerializedName("landmark") val landmark: String = "",
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("addressType") val addressType: String? = "HOME",
    @SerializedName("isDefault") val isDefault: Boolean = false
) {
    fun toDomain(): Address = Address(
        id = id,
        label = "",
        fullName = name,
        phone = phone,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        city = city,
        state = state,
        pincode = pincode,
        landmark = landmark,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault,
        type = addressType ?: "HOME"
    )
}

fun Address.toCreateRequest(): CreateAddressRequest = CreateAddressRequest(
    name = fullName,
    phone = phone,
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    city = city,
    state = state,
    pincode = pincode,
    landmark = landmark,
    latitude = latitude,
    longitude = longitude,
    addressType = type,
    isDefault = isDefault
)
