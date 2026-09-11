package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.ServiceablePincode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceablePincodeRepository : JpaRepository<ServiceablePincode, Long> {
    fun findByPincodeAndIsActiveTrue(pincode: String): ServiceablePincode?
    fun existsByPincodeAndIsActiveTrue(pincode: String): Boolean
}
