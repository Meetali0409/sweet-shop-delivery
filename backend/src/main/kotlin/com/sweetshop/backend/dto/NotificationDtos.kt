package com.sweetshop.backend.dto

import com.sweetshop.backend.entity.NotificationType
import java.time.LocalDateTime

data class NotificationDto(
    val id: Long,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean,
    val referenceId: Long?,
    val createdAt: LocalDateTime
)
