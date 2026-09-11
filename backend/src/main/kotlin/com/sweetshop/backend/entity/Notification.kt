package com.sweetshop.backend.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    var message: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: NotificationType,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "reference_id")
    var referenceId: Long? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Notification(id=$id, title=$title, type=$type)"
}
