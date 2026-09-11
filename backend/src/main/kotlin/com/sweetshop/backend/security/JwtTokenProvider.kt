package com.sweetshop.backend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret}")
    private val jwtSecret: String,

    @Value("\${app.jwt.issuer:sweetshop-api}")
    private val issuer: String,

    @Value("\${app.jwt.audience:sweetshop-clients}")
    private val audience: String,

    @Value("\${app.jwt.access-token-expiration}")
    private val accessTokenExpirationMs: Long,

    @Value("\${app.jwt.refresh-token-expiration}")
    private val refreshTokenExpirationMs: Long
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private fun getSigningKey(): SecretKey {
        val keyBytes = jwtSecret.toByteArray()
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateAccessToken(userId: Long, email: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpirationMs)

        return Jwts.builder()
            .subject(userId.toString())
            .issuer(issuer)
            .audience().add(audience).and()
            .claim("email", email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpirationMs)

        return Jwts.builder()
            .subject(userId.toString())
            .issuer(issuer)
            .audience().add(audience).and()
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = getClaims(token)
        return claims.subject.toLong()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature: {}", ex.message)
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", ex.message)
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token: {}", ex.message)
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token: {}", ex.message)
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", ex.message)
        }
        return false
    }

    fun getRefreshTokenExpirationMs(): Long = refreshTokenExpirationMs

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .requireIssuer(issuer)
            .requireAudience(audience)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
