package com.sweetshop.backend.service

import com.sweetshop.backend.dto.AddressDto
import com.sweetshop.backend.dto.CreateAddressRequest
import com.sweetshop.backend.dto.UpdateAddressRequest
import com.sweetshop.backend.entity.Address
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.AddressRepository
import com.sweetshop.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AddressService(
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(AddressService::class.java)

    fun getAddresses(userId: Long): List<AddressDto> {
        return addressRepository.findByUserId(userId).map { it.toDto() }
    }

    @Transactional
    fun createAddress(userId: Long, request: CreateAddressRequest): AddressDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        if (request.isDefault) {
            val currentDefault = addressRepository.findByUserIdAndIsDefaultTrue(userId)
            currentDefault?.let {
                it.isDefault = false
                addressRepository.save(it)
            }
        }

        val address = Address(
            user = user,
            name = request.name,
            phone = request.phone,
            addressLine1 = request.addressLine1,
            addressLine2 = request.addressLine2,
            city = request.city,
            state = request.state,
            pincode = request.pincode,
            landmark = request.landmark,
            isDefault = request.isDefault
        )

        val savedAddress = addressRepository.save(address)
        logger.info("Address created: id={}, userId={}", savedAddress.id, userId)
        return savedAddress.toDto()
    }

    @Transactional
    fun updateAddress(userId: Long, addressId: Long, request: UpdateAddressRequest): AddressDto {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address not found with id: $addressId") }

        if (address.user.id != userId) {
            throw BadRequestException("Address does not belong to this user")
        }

        request.name?.let { address.name = it }
        request.phone?.let { address.phone = it }
        request.addressLine1?.let { address.addressLine1 = it }
        request.addressLine2?.let { address.addressLine2 = it }
        request.city?.let { address.city = it }
        request.state?.let { address.state = it }
        request.pincode?.let { address.pincode = it }
        request.landmark?.let { address.landmark = it }
        request.isDefault?.let { isDefault ->
            if (isDefault) {
                val currentDefault = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                currentDefault?.let {
                    if (it.id != addressId) {
                        it.isDefault = false
                        addressRepository.save(it)
                    }
                }
            }
            address.isDefault = isDefault
        }

        val savedAddress = addressRepository.save(address)
        logger.info("Address updated: id={}, userId={}", savedAddress.id, userId)
        return savedAddress.toDto()
    }

    @Transactional
    fun deleteAddress(userId: Long, addressId: Long) {
        val address = addressRepository.findById(addressId)
            .orElseThrow { ResourceNotFoundException("Address not found with id: $addressId") }

        if (address.user.id != userId) {
            throw BadRequestException("Address does not belong to this user")
        }

        addressRepository.delete(address)
        logger.info("Address deleted: id={}, userId={}", addressId, userId)
    }
}
