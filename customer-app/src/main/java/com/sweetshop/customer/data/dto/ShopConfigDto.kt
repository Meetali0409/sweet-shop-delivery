package com.sweetshop.customer.data.dto

data class ShopConfigDto(
    val shopName: String = "Sweet Shop",
    val tagline: String? = null,
    val logoUrl: String? = null,
    val primaryColor: String? = null,
    val secondaryColor: String? = null,
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val countryCode: String = "IN",
    val taxRate: Double = 5.0,
    val supportEmail: String? = null,
    val supportPhone: String? = null,
    val termsUrl: String? = null,
    val privacyUrl: String? = null,
    val aboutText: String? = null,
    val paymentMethods: List<String> = listOf("COD"),
    val deliveryCharge: Double? = null,
    val freeDeliveryThreshold: Double? = null
)
