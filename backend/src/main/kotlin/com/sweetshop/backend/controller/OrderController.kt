package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.entity.OrderStatus
import com.sweetshop.backend.security.UserPrincipal
import com.sweetshop.backend.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: CreateOrderRequest
    ): ResponseEntity<ApiResponse<OrderDto>> {
        val order = orderService.createOrder(userPrincipal.id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = order, message = "Order placed successfully")
        )
    }

    @GetMapping
    fun getOrders(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<OrderListDto>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val orders = orderService.getOrders(userPrincipal.id, status, pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = orders))
    }

    @GetMapping("/{id}")
    fun getOrderById(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<OrderDto>> {
        val order = orderService.getOrderById(userPrincipal.id, id)
        return ResponseEntity.ok(ApiResponse(success = true, data = order))
    }

    @PostMapping("/{id}/cancel")
    fun cancelOrder(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<OrderDto>> {
        val order = orderService.cancelOrder(userPrincipal.id, id)
        return ResponseEntity.ok(ApiResponse(success = true, data = order, message = "Order cancelled successfully"))
    }

    @PostMapping("/{id}/reorder")
    fun reorder(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<ReorderResponse>> {
        val response = orderService.reorder(userPrincipal.id, id)
        return ResponseEntity.ok(ApiResponse(success = true, data = response, message = response.message))
    }
}
