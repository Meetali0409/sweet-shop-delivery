package com.sweetshop.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ShopConfigDto(
    val shopName: String,
    val tagline: String?,
    val logoUrl: String?,
    val primaryColor: String?,
    val secondaryColor: String?,
    val currencyCode: String,
    val currencySymbol: String,
    val countryCode: String,
    val taxRate: BigDecimal,
    val supportEmail: String?,
    val supportPhone: String?,
    val termsUrl: String?,
    val privacyUrl: String?,
    val aboutText: String?,
    val paymentMethods: List<String>,
    val deliveryCharge: BigDecimal?,
    val freeDeliveryThreshold: BigDecimal?,
    val shopLatitude: BigDecimal?,
    val shopLongitude: BigDecimal?,
    val deliveryRadiusKm: BigDecimal?,
    val perKmCharge: BigDecimal?,
    val baseDeliveryDistanceKm: BigDecimal?,
    val estimatedDeliveryDays: Int?
)

data class UpdateShopConfigRequest(
    @field:NotBlank(message = "Shop name is required")
    @field:Size(max = 200, message = "Shop name must be at most 200 characters")
    val shopName: String?,

    @field:Size(max = 500)
    val tagline: String?,

    val logoUrl: String?,

    @field:Size(max = 7)
    val primaryColor: String?,

    @field:Size(max = 7)
    val secondaryColor: String?,

    @field:Size(max = 3)
    val currencyCode: String?,

    @field:Size(max = 5)
    val currencySymbol: String?,

    @field:Size(max = 3)
    val countryCode: String?,

    val taxRate: BigDecimal?,

    val supportEmail: String?,
    val supportPhone: String?,
    val termsUrl: String?,
    val privacyUrl: String?,
    val aboutText: String?,
    val paymentMethods: List<String>?,
    val deliveryCharge: BigDecimal?,
    val freeDeliveryThreshold: BigDecimal?,
    val shopLatitude: BigDecimal?,
    val shopLongitude: BigDecimal?,
    val deliveryRadiusKm: BigDecimal?,
    val perKmCharge: BigDecimal?,
    val baseDeliveryDistanceKm: BigDecimal?,
    val estimatedDeliveryDays: Int?
)
