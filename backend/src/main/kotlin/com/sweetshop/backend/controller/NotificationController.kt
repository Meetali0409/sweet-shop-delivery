package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.security.UserPrincipal
import com.sweetshop.backend.service.NotificationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping
    fun getNotifications(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<NotificationDto>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val notifications = notificationService.getNotifications(userPrincipal.id, pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = notifications))
    }

    @GetMapping("/unread-count")
    fun getUnreadCount(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ApiResponse<Long>> {
        val count = notificationService.getUnreadCount(userPrincipal.id)
        return ResponseEntity.ok(ApiResponse(success = true, data = count))
    }

    @PutMapping("/{id}/read")
    fun markAsRead(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        notificationService.markAsRead(userPrincipal.id, id)
        return ResponseEntity.ok(ApiResponse(success = true, data = null, message = "Notification marked as read"))
    }
}
