package com.sweetshop.backend.util

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class OrderNumberGenerator {

    fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random.nextInt(1000, 9999)
        return "ORD-${timestamp}-${random}"
    }
}
