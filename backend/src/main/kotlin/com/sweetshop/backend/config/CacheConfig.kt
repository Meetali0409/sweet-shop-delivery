package com.sweetshop.backend.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig {

    companion object {
        const val CATEGORIES_CACHE = "categories"
        const val PRODUCTS_CACHE = "products"
        const val FEATURED_PRODUCTS_CACHE = "featuredProducts"
        const val BESTSELLER_PRODUCTS_CACHE = "bestsellerProducts"
        const val SHOP_CONFIG_CACHE = "shopConfig"
    }

    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager(
            CATEGORIES_CACHE,
            PRODUCTS_CACHE,
            FEATURED_PRODUCTS_CACHE,
            BESTSELLER_PRODUCTS_CACHE,
            SHOP_CONFIG_CACHE
        )
    }
}
