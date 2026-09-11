package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.DashboardDto
import com.sweetshop.admin.data.dto.SalesReportDto
import com.sweetshop.admin.util.Resource

interface ReportRepository {
    suspend fun getDashboard(): Resource<DashboardDto>
    suspend fun getSalesOverview(period: String): Resource<List<SalesReportDto>>
    suspend fun getSalesReport(from: String, to: String): Resource<SalesReportDto>
}
