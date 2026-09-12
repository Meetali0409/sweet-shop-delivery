package com.sweetshop.backend.controller

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminReportController(
    private val reportService: ReportService
) {

    @GetMapping("/dashboard")
    fun getDashboard(): ResponseEntity<ApiResponse<DashboardDto>> {
        val dashboard = reportService.getDashboard()
        return ResponseEntity.ok(ApiResponse(success = true, data = dashboard))
    }

    @GetMapping("/reports/sales")
    fun getSalesReport(
        @RequestParam from: String,
        @RequestParam to: String
    ): ResponseEntity<ApiResponse<SalesReportDto>> {
        val fromDate = LocalDate.parse(from).atStartOfDay()
        val toDate = LocalDate.parse(to).atTime(LocalTime.MAX)
        val report = reportService.getSalesReport(fromDate, toDate)
        return ResponseEntity.ok(ApiResponse(success = true, data = report))
    }

    @GetMapping("/reports/sales-overview")
    fun getSalesOverview(
        @RequestParam(defaultValue = "daily") period: String
    ): ResponseEntity<ApiResponse<List<SalesReportDto>>> {
        val overview = reportService.getSalesOverview(period)
        return ResponseEntity.ok(ApiResponse(success = true, data = overview))
    }
}
