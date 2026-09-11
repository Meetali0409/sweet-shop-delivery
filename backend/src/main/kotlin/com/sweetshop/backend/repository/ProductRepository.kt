package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Product
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    fun findByIsAvailableTrue(pageable: Pageable): Page<Product>

    fun findByCategoryIdAndIsAvailableTrue(categoryId: Long, pageable: Pageable): Page<Product>

    fun findByIsFeaturedTrueAndIsAvailableTrue(pageable: Pageable): Page<Product>

    fun findByIsBestsellerTrueAndIsAvailableTrue(pageable: Pageable): Page<Product>

    @Query(
        "SELECT p FROM Product p WHERE p.isAvailable = true AND " +
        "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    fun searchProducts(@Param("search") search: String, pageable: Pageable): Page<Product>

    fun findByStockQuantityLessThanAndIsAvailableTrue(threshold: Int): List<Product>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    fun findByIdForUpdate(@Param("id") id: Long): Product?

    fun countByCategoryId(categoryId: Long): Long
}
