package com.sweetshop.backend.service

import com.sweetshop.backend.dto.NotificationDto
import com.sweetshop.backend.dto.PagedResponse
import com.sweetshop.backend.entity.Notification
import com.sweetshop.backend.entity.NotificationType
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.NotificationRepository
import com.sweetshop.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun getNotifications(userId: Long, pageable: Pageable): PagedResponse<NotificationDto> {
        val page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
        return PagedResponse(
            content = page.content.map { it.toDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    fun getUnreadCount(userId: Long): Long {
        return notificationRepository.countByUserIdAndIsReadFalse(userId)
    }

    @Transactional
    fun markAsRead(userId: Long, notificationId: Long) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { ResourceNotFoundException("Notification not found with id: $notificationId") }

        if (notification.user.id != userId) {
            throw ResourceNotFoundException("Notification not found with id: $notificationId")
        }

        notification.isRead = true
        notificationRepository.save(notification)
        logger.debug("Notification marked as read: id={}, userId={}", notificationId, userId)
    }

    @Transactional
    fun createNotification(
        userId: Long,
        title: String,
        message: String,
        type: NotificationType,
        referenceId: Long? = null
    ) {
        val user = userRepository.findById(userId).orElse(null) ?: return

        val notification = Notification(
            user = user,
            title = title,
            message = message,
            type = type,
            referenceId = referenceId
        )

        notificationRepository.save(notification)
        logger.debug("Notification created: userId={}, type={}, title={}", userId, type, title)
    }
}
