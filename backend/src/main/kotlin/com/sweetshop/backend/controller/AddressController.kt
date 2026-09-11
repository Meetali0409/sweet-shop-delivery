package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.security.UserPrincipal
import com.sweetshop.backend.service.AddressService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/addresses")
class AddressController(
    private val addressService: AddressService
) {

    @GetMapping
    fun getAddresses(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ApiResponse<List<AddressDto>>> {
        val addresses = addressService.getAddresses(userPrincipal.id)
        return ResponseEntity.ok(ApiResponse(success = true, data = addresses))
    }

    @PostMapping
    fun createAddress(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: CreateAddressRequest
    ): ResponseEntity<ApiResponse<AddressDto>> {
        val address = addressService.createAddress(userPrincipal.id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(success = true, data = address, message = "Address created successfully")
        )
    }

    @PutMapping("/{id}")
    fun updateAddress(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAddressRequest
    ): ResponseEntity<ApiResponse<AddressDto>> {
        val address = addressService.updateAddress(userPrincipal.id, id, request)
        return ResponseEntity.ok(ApiResponse(success = true, data = address, message = "Address updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteAddress(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Nothing>> {
        addressService.deleteAddress(userPrincipal.id, id)
        return ResponseEntity.ok(ApiResponse(success = true, data = null, message = "Address deleted successfully"))
    }
}
