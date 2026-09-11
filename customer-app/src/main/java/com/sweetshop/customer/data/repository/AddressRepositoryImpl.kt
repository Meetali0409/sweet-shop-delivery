package com.sweetshop.customer.data.repository

import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.data.dto.toCreateRequest
import com.sweetshop.customer.domain.model.Address
import com.sweetshop.customer.domain.repository.AddressRepository
import com.sweetshop.customer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val api: SweetShopApi
) : AddressRepository {

    override suspend fun getAddresses(): Resource<List<Address>> {
        return try {
            val response = api.getAddresses()
            if (response.isSuccessful && response.body()?.success == true) {
                val addresses = response.body()!!.data!!.map { it.toDomain() }
                Resource.Success(addresses)
            } else {
                Resource.Error(response.body()?.error ?: "Failed to load addresses")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun createAddress(address: Address): Resource<Address> {
        return try {
            val response = api.createAddress(address.toCreateRequest())
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to create address")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun updateAddress(addressId: Long, address: Address): Resource<Address> {
        return try {
            val response = api.updateAddress(addressId, address.toCreateRequest())
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to update address")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun deleteAddress(addressId: Long): Resource<Unit> {
        return try {
            val response = api.deleteAddress(addressId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to delete address")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    override suspend fun setDefaultAddress(addressId: Long): Resource<Address> {
        return try {
            val response = api.setDefaultAddress(addressId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!.toDomain())
            } else {
                Resource.Error(response.body()?.error ?: "Failed to set default address")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
