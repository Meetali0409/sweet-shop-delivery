package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "shop_config")
class ShopConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "shop_name", nullable = false)
    var shopName: String = "Sweet Shop",

    @Column(name = "tagline")
    var tagline: String? = "Delicious sweets delivered to your door",

    @Column(name = "logo_url")
    var logoUrl: String? = null,

    @Column(name = "primary_color")
    var primaryColor: String? = "#E91E63",

    @Column(name = "secondary_color")
    var secondaryColor: String? = "#FF5722",

    @Column(name = "currency_code", nullable = false)
    var currencyCode: String = "INR",

    @Column(name = "currency_symbol", nullable = false)
    var currencySymbol: String = "₹",

    @Column(name = "country_code", nullable = false)
    var countryCode: String = "IN",

    @Column(name = "tax_rate", nullable = false)
    var taxRate: BigDecimal = BigDecimal("5.00"),

    @Column(name = "support_email")
    var supportEmail: String? = null,

    @Column(name = "support_phone")
    var supportPhone: String? = null,

    @Column(name = "terms_url")
    var termsUrl: String? = null,

    @Column(name = "privacy_url")
    var privacyUrl: String? = null,

    @Column(name = "about_text", columnDefinition = "TEXT")
    var aboutText: String? = null,

    @Column(name = "payment_methods", nullable = false)
    var paymentMethods: String = "COD,ONLINE",

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
