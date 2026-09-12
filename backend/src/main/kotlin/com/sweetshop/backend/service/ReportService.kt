package com.sweetshop.backend.service

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.entity.OrderStatus
import com.sweetshop.backend.mapper.toListDto
import com.sweetshop.backend.repository.OrderRepository
import com.sweetshop.backend.repository.ProductRepository
import com.sweetshop.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(ReportService::class.java)

    fun getSalesReport(from: LocalDateTime, to: LocalDateTime): SalesReportDto {
        val revenue = orderRepository.sumTotalAmountByCreatedAtBetween(from, to)
        val orders = orderRepository.countByCreatedAtBetween(from, to)
        val averageOrderValue = if (orders > 0) {
            revenue.divide(BigDecimal(orders), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val periodLabel = "${from.format(formatter)} - ${to.format(formatter)}"

        return SalesReportDto(
            revenue = revenue,
            orders = orders,
            averageOrderValue = averageOrderValue,
            periodLabel = periodLabel
        )
    }

    fun getDashboard(): DashboardDto {
        val totalOrders = orderRepository.count()
        val totalRevenue = orderRepository.sumTotalAmountByCreatedAtBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0),
            LocalDateTime.now()
        )
        val totalCustomers = userRepository.count()
        val totalProducts = productRepository.count()
        val pendingOrders = orderRepository.countByOrderStatus(OrderStatus.PLACED)

        val recentOrdersPage = orderRepository.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        )
        val recentOrders = recentOrdersPage.content.map { it.toListDto() }

        val lowStockProducts = productRepository.findByStockQuantityLessThanAndIsAvailableTrue(10)
            .map { product ->
                InventoryItemDto(
                    productId = product.id,
                    productName = product.name,
                    currentStock = product.stockQuantity,
                    status = when {
                        product.stockQuantity <= 0 -> StockStatus.OUT_OF_STOCK
                        product.stockQuantity < 10 -> StockStatus.LOW_STOCK
                        else -> StockStatus.IN_STOCK
                    }
                )
            }

        return DashboardDto(
            totalOrders = totalOrders,
            totalRevenue = totalRevenue,
            totalCustomers = totalCustomers,
            totalProducts = totalProducts,
            pendingOrders = pendingOrders,
            recentOrders = recentOrders,
            lowStockProducts = lowStockProducts
        )
    }

    fun getSalesOverview(period: String): List<SalesReportDto> {
        val now = LocalDate.now()
        val reports = mutableListOf<SalesReportDto>()
        val formatter = DateTimeFormatter.ofPattern("dd MMM")

        when (period.lowercase()) {
            "daily" -> {
                for (i in 6 downTo 0) {
                    val date = now.minusDays(i.toLong())
                    val from = date.atStartOfDay()
                    val to = date.atTime(LocalTime.MAX)

                    val revenue = orderRepository.sumTotalAmountByCreatedAtBetween(from, to)
                    val orders = orderRepository.countByCreatedAtBetween(from, to)
                    val avg = if (orders > 0) revenue.divide(BigDecimal(orders), 2, RoundingMode.HALF_UP)
                             else BigDecimal.ZERO

                    reports.add(
                        SalesReportDto(
                            revenue = revenue,
                            orders = orders,
                            averageOrderValue = avg,
                            periodLabel = date.format(formatter)
                        )
                    )
                }
            }
            "weekly" -> {
                for (i in 3 downTo 0) {
                    val weekStart = now.minusWeeks(i.toLong()).with(java.time.DayOfWeek.MONDAY)
                    val weekEnd = weekStart.plusDays(6)
                    val from = weekStart.atStartOfDay()
                    val to = weekEnd.atTime(LocalTime.MAX)

                    val revenue = orderRepository.sumTotalAmountByCreatedAtBetween(from, to)
                    val orders = orderRepository.countByCreatedAtBetween(from, to)
                    val avg = if (orders > 0) revenue.divide(BigDecimal(orders), 2, RoundingMode.HALF_UP)
                             else BigDecimal.ZERO

                    reports.add(
                        SalesReportDto(
                            revenue = revenue,
                            orders = orders,
                            averageOrderValue = avg,
                            periodLabel = "${weekStart.format(formatter)} - ${weekEnd.format(formatter)}"
                        )
                    )
                }
            }
            "monthly" -> {
                for (i in 5 downTo 0) {
                    val monthStart = now.minusMonths(i.toLong()).withDayOfMonth(1)
                    val monthEnd = monthStart.plusMonths(1).minusDays(1)
                    val from = monthStart.atStartOfDay()
                    val to = monthEnd.atTime(LocalTime.MAX)

                    val revenue = orderRepository.sumTotalAmountByCreatedAtBetween(from, to)
                    val orders = orderRepository.countByCreatedAtBetween(from, to)
                    val avg = if (orders > 0) revenue.divide(BigDecimal(orders), 2, RoundingMode.HALF_UP)
                             else BigDecimal.ZERO

                    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
                    reports.add(
                        SalesReportDto(
                            revenue = revenue,
                            orders = orders,
                            averageOrderValue = avg,
                            periodLabel = monthStart.format(monthFormatter)
                        )
                    )
                }
            }
            else -> {
                logger.warn("Unknown period: {}, defaulting to daily", period)
                return getSalesOverview("daily")
            }
        }

        return reports
    }
}
