package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {

    fun findByOrderId(orderId: Long): List<OrderItem>

    @Query(
        "SELECT oi.product.id, oi.productName, SUM(oi.quantity) as totalQty " +
        "FROM OrderItem oi GROUP BY oi.product.id, oi.productName " +
        "ORDER BY totalQty DESC"
    )
    fun findBestSellingProducts(): List<Array<Any>>
}
