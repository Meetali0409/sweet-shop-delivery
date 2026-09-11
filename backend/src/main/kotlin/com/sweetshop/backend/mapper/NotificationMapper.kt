package com.sweetshop.backend.mapper

import com.sweetshop.backend.dto.NotificationDto
import com.sweetshop.backend.entity.Notification

fun Notification.toDto(): NotificationDto = NotificationDto(
    id = this.id,
    title = this.title,
    message = this.message,
    type = this.type,
    isRead = this.isRead,
    referenceId = this.referenceId,
    createdAt = this.createdAt
)
