package com.sweetshop.backend.util

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class OrderNumberGenerator {

    fun generateOrderNumber(): String {
        val digits = Random.nextInt(10000, 99999)
        return "ORD-$digits"
    }
}
