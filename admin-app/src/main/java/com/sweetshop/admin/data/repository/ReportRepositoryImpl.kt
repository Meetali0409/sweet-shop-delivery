package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.DashboardDto
import com.sweetshop.admin.data.dto.SalesReportDto
import com.sweetshop.admin.domain.repository.ReportRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : ReportRepository {

    override suspend fun getDashboard(): Resource<DashboardDto> {
        return try {
            val response = api.getDashboard()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load dashboard")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getSalesOverview(period: String): Resource<List<SalesReportDto>> {
        return try {
            val response = api.getSalesOverview(period)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load sales overview")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getSalesReport(from: String, to: String): Resource<SalesReportDto> {
        return try {
            val response = api.getSalesReport(from, to)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load sales report")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
