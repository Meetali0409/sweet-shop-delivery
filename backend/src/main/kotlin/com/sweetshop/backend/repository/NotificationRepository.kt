package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long, pageable: Pageable): Page<Notification>
    fun countByUserIdAndIsReadFalse(userId: Long): Long
}
