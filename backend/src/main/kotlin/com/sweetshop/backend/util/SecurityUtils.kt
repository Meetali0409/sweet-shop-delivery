package com.sweetshop.backend.util

import com.sweetshop.backend.security.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun getCurrentUserId(): Long {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        if (principal is UserPrincipal) {
            return principal.id
        }
        throw IllegalStateException("User not authenticated")
    }

    fun getCurrentUser(): UserPrincipal {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        if (principal is UserPrincipal) {
            return principal
        }
        throw IllegalStateException("User not authenticated")
    }
}
