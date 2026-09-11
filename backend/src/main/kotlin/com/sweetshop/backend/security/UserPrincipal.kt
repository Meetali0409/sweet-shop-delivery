package com.sweetshop.backend.security

import com.sweetshop.backend.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    val id: Long,
    val email: String,
    private val pwd: String,
    private val authorities: MutableCollection<out GrantedAuthority>
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = pwd

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = mutableListOf(
                SimpleGrantedAuthority("ROLE_${user.role.name}")
            )
            return UserPrincipal(
                id = user.id,
                email = user.email,
                pwd = user.passwordHash,
                authorities = authorities
            )
        }
    }
}
