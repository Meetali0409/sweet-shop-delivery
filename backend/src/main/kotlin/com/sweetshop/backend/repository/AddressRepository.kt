package com.sweetshop.backend.repository

import com.sweetshop.backend.entity.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : JpaRepository<Address, Long> {
    fun findByUserId(userId: Long): List<Address>
    fun findByUserIdAndIsDefaultTrue(userId: Long): Address?
}
