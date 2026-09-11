package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.ApiResponse
import com.sweetshop.backend.dto.ShopConfigDto
import com.sweetshop.backend.dto.UpdateShopConfigRequest
import com.sweetshop.backend.service.ShopConfigService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class ShopConfigController(
    private val shopConfigService: ShopConfigService
) {

    @GetMapping("/config")
    fun getConfig(): ResponseEntity<ApiResponse<ShopConfigDto>> {
        val config = shopConfigService.getConfig()
        return ResponseEntity.ok(ApiResponse(success = true, data = config))
    }

    @PutMapping("/admin/config")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateConfig(
        @Valid @RequestBody request: UpdateShopConfigRequest
    ): ResponseEntity<ApiResponse<ShopConfigDto>> {
        val config = shopConfigService.updateConfig(request)
        return ResponseEntity.ok(ApiResponse(success = true, data = config))
    }
}
