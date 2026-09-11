package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByIsActiveTrueOrderBySortOrder(): List<Category>
}
