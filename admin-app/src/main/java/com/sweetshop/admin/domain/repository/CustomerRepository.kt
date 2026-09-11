package com.sweetshop.admin.domain.repository

import com.sweetshop.admin.data.dto.CustomerListDto
import com.sweetshop.admin.data.dto.PagedResponse
import com.sweetshop.admin.data.dto.UserDto
import com.sweetshop.admin.util.Resource

interface CustomerRepository {
    suspend fun getCustomers(page: Int, size: Int, search: String?): Resource<PagedResponse<CustomerListDto>>
    suspend fun getCustomer(id: Long): Resource<CustomerListDto>
    suspend fun toggleCustomerStatus(id: Long): Resource<UserDto>
}
