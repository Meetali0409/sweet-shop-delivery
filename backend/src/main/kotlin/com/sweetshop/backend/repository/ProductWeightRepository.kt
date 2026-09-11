package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.ProductWeight
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductWeightRepository : JpaRepository<ProductWeight, Long> {
    fun findByProductId(productId: Long): List<ProductWeight>
}
