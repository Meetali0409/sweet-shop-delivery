package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.entity.OrderStatus
import com.sweetshop.backend.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
class AdminOrderController(
    private val orderService: OrderService
) {

    @GetMapping
    fun getAllOrders(
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<OrderListDto>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val orders = orderService.getAllOrders(status, pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = orders))
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderDto>> {
        val order = orderService.getAdminOrderById(id)
        return ResponseEntity.ok(ApiResponse(success = true, data = order))
    }

    @PatchMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<ApiResponse<OrderDto>> {
        val order = orderService.updateOrderStatus(id, request)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = order, message = "Order status updated successfully")
        )
    }
}
