package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Order
import com.sweetshop.backend.entity.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {

    fun findByUserId(userId: Long, pageable: Pageable): Page<Order>

    fun findByOrderNumber(orderNumber: String): Order?

    fun findByOrderStatus(status: OrderStatus, pageable: Pageable): Page<Order>

    fun findByUserIdAndOrderStatus(userId: Long, status: OrderStatus, pageable: Pageable): Page<Order>

    fun countByUserId(userId: Long): Long

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    fun countByCreatedAtBetween(@Param("from") from: LocalDateTime, @Param("to") to: LocalDateTime): Long

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    fun sumTotalAmountByCreatedAtBetween(@Param("from") from: LocalDateTime, @Param("to") to: LocalDateTime): BigDecimal

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :from AND :to AND o.orderStatus <> :status")
    fun countByCreatedAtBetweenAndOrderStatusNot(@Param("from") from: LocalDateTime, @Param("to") to: LocalDateTime, @Param("status") status: OrderStatus): Long

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt BETWEEN :from AND :to AND o.orderStatus <> :status")
    fun sumTotalAmountByCreatedAtBetweenAndOrderStatusNot(@Param("from") from: LocalDateTime, @Param("to") to: LocalDateTime, @Param("status") status: OrderStatus): BigDecimal

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.id = :userId")
    fun sumTotalAmountByUserId(@Param("userId") userId: Long): BigDecimal

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    fun countByOrderStatus(@Param("status") status: OrderStatus): Long
}
