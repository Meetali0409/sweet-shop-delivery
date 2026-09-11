package com.sweetshop.admin.data.repository

import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.CustomerListDto
import com.sweetshop.admin.data.dto.PagedResponse
import com.sweetshop.admin.data.dto.UserDto
import com.sweetshop.admin.domain.repository.CustomerRepository
import com.sweetshop.admin.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : CustomerRepository {

    override suspend fun getCustomers(
        page: Int,
        size: Int,
        search: String?
    ): Resource<PagedResponse<CustomerListDto>> {
        return try {
            val response = api.getCustomers(page, size, search)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load customers")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun getCustomer(id: Long): Resource<CustomerListDto> {
        return try {
            val response = api.getCustomer(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun toggleCustomerStatus(id: Long): Resource<UserDto> {
        return try {
            val response = api.toggleCustomerStatus(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to toggle customer status")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
