package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
class AdminCustomerController(
    private val userService: UserService
) {

    @GetMapping
    fun getCustomers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<PagedResponse<CustomerListDto>>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val customers = userService.getCustomers(pageable)
        return ResponseEntity.ok(ApiResponse(success = true, data = customers))
    }

    @GetMapping("/{id}")
    fun getCustomerDetails(@PathVariable id: Long): ResponseEntity<ApiResponse<CustomerListDto>> {
        val customer = userService.getCustomerDetails(id)
        return ResponseEntity.ok(ApiResponse(success = true, data = customer))
    }

    @PatchMapping("/{id}/toggle-status")
    fun toggleCustomerStatus(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.toggleCustomerStatus(id)
        return ResponseEntity.ok(
            ApiResponse(success = true, data = user, message = "Customer status updated")
        )
    }
}
