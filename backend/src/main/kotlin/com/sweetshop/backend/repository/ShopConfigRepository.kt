package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.ShopConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShopConfigRepository : JpaRepository<ShopConfig, Long>
