package com.sweetshop.customer.domain.repository

import com.sweetshop.customer.domain.model.Address
import com.sweetshop.customer.util.Resource

interface AddressRepository {
    suspend fun getAddresses(): Resource<List<Address>>
    suspend fun createAddress(address: Address): Resource<Address>
    suspend fun updateAddress(addressId: Long, address: Address): Resource<Address>
    suspend fun deleteAddress(addressId: Long): Resource<Unit>
    suspend fun setDefaultAddress(addressId: Long): Resource<Address>
}
