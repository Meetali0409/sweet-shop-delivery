package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.DeliveryConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeliveryConfigRepository : JpaRepository<DeliveryConfig, Long>
