package com.sweetshop.backend.util

import java.math.BigDecimal
import kotlin.math.*

object GeoUtils {

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the Haversine distance between two lat/lng points in kilometers.
     */
    fun haversineDistanceKm(
        lat1: BigDecimal, lon1: BigDecimal,
        lat2: BigDecimal, lon2: BigDecimal
    ): Double {
        val lat1Rad = Math.toRadians(lat1.toDouble())
        val lat2Rad = Math.toRadians(lat2.toDouble())
        val dLat = Math.toRadians(lat2.toDouble() - lat1.toDouble())
        val dLon = Math.toRadians(lon2.toDouble() - lon1.toDouble())

        val a = sin(dLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_KM * c
    }
}
