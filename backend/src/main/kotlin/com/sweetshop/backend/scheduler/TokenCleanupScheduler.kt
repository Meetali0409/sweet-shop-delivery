package com.sweetshop.backend.scheduler

import com.sweetshop.backend.repository.RefreshTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class TokenCleanupScheduler(
    private val refreshTokenRepository: RefreshTokenRepository
) {
    private val logger = LoggerFactory.getLogger(TokenCleanupScheduler::class.java)

    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    fun cleanExpiredRefreshTokens() {
        val now = LocalDateTime.now()
        val deleted = refreshTokenRepository.deleteByExpiresAtBefore(now)
        if (deleted > 0) {
            logger.info("Cleaned up {} expired refresh tokens", deleted)
        }
    }
}
