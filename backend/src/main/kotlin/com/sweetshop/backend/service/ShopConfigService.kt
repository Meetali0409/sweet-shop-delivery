package com.sweetshop.backend.service

import com.sweetshop.backend.config.CacheConfig
import com.sweetshop.backend.dto.ShopConfigDto
import com.sweetshop.backend.dto.UpdateShopConfigRequest
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.repository.DeliveryConfigRepository
import com.sweetshop.backend.repository.ShopConfigRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ShopConfigService(
    private val shopConfigRepository: ShopConfigRepository,
    private val deliveryConfigRepository: DeliveryConfigRepository
) {
    private val logger = LoggerFactory.getLogger(ShopConfigService::class.java)

    @Cacheable(CacheConfig.SHOP_CONFIG_CACHE)
    fun getConfig(): ShopConfigDto {
        val config = shopConfigRepository.findAll().firstOrNull()
            ?: throw ResourceNotFoundException("Shop configuration not found")

        val deliveryConfig = deliveryConfigRepository.findAll().firstOrNull()

        return ShopConfigDto(
            shopName = config.shopName,
            tagline = config.tagline,
            logoUrl = config.logoUrl,
            primaryColor = config.primaryColor,
            secondaryColor = config.secondaryColor,
            currencyCode = config.currencyCode,
            currencySymbol = config.currencySymbol,
            countryCode = config.countryCode,
            taxRate = config.taxRate,
            supportEmail = config.supportEmail,
            supportPhone = config.supportPhone,
            termsUrl = config.termsUrl,
            privacyUrl = config.privacyUrl,
            aboutText = config.aboutText,
            paymentMethods = config.paymentMethods.split(",").map { it.trim() },
            deliveryCharge = deliveryConfig?.deliveryCharge,
            freeDeliveryThreshold = deliveryConfig?.freeDeliveryThreshold
        )
    }

    @Transactional
    @CacheEvict(CacheConfig.SHOP_CONFIG_CACHE, allEntries = true)
    fun updateConfig(request: UpdateShopConfigRequest): ShopConfigDto {
        val config = shopConfigRepository.findAll().firstOrNull()
            ?: throw ResourceNotFoundException("Shop configuration not found")

        request.shopName?.let { config.shopName = it }
        request.tagline?.let { config.tagline = it }
        request.logoUrl?.let { config.logoUrl = it }
        request.primaryColor?.let { config.primaryColor = it }
        request.secondaryColor?.let { config.secondaryColor = it }
        request.currencyCode?.let { config.currencyCode = it }
        request.currencySymbol?.let { config.currencySymbol = it }
        request.countryCode?.let { config.countryCode = it }
        request.taxRate?.let { config.taxRate = it }
        request.supportEmail?.let { config.supportEmail = it }
        request.supportPhone?.let { config.supportPhone = it }
        request.termsUrl?.let { config.termsUrl = it }
        request.privacyUrl?.let { config.privacyUrl = it }
        request.aboutText?.let { config.aboutText = it }
        request.paymentMethods?.let { config.paymentMethods = it.joinToString(",") }
        config.updatedAt = LocalDateTime.now()

        shopConfigRepository.save(config)
        logger.info("Shop configuration updated: {}", config.shopName)

        return getConfig()
    }
}
