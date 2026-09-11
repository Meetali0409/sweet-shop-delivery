package com.sweetshop.backend.service

import com.sweetshop.backend.dto.CustomerListDto
import com.sweetshop.backend.dto.PagedResponse
import com.sweetshop.backend.dto.UserDto
import com.sweetshop.backend.dto.UserProfileUpdateRequest
import com.sweetshop.backend.entity.UserRole
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toCustomerListDto
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.OrderRepository
import com.sweetshop.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun getUserById(id: Long): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        return user.toDto()
    }

    @Transactional
    fun updateProfile(userId: Long, request: UserProfileUpdateRequest): UserDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        request.name?.let { user.name = it }
        request.phone?.let { user.phone = it }
        request.profileImage?.let { user.profileImage = it }

        val savedUser = userRepository.save(user)
        logger.info("Profile updated for user: {}", savedUser.email)
        return savedUser.toDto()
    }

    fun getCustomers(pageable: Pageable): PagedResponse<CustomerListDto> {
        val page = userRepository.findAll(pageable)
        val customers = page.content
            .filter { it.role == UserRole.CUSTOMER }
            .map { user ->
                val totalOrders = orderRepository.countByUserId(user.id)
                val totalSpending = orderRepository.sumTotalAmountByUserId(user.id)
                user.toCustomerListDto(totalOrders, totalSpending)
            }

        return PagedResponse(
            content = customers,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    fun getCustomerDetails(id: Long): CustomerListDto {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Customer not found with id: $id") }

        val totalOrders = orderRepository.countByUserId(user.id)
        val totalSpending = orderRepository.sumTotalAmountByUserId(user.id)
        return user.toCustomerListDto(totalOrders, totalSpending)
    }

    @Transactional
    fun toggleCustomerStatus(id: Long): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Customer not found with id: $id") }

        user.isActive = !user.isActive
        val savedUser = userRepository.save(user)
        logger.info("Customer status toggled: id={}, isActive={}", id, savedUser.isActive)
        return savedUser.toDto()
    }
}
