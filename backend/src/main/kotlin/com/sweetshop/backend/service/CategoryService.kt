package com.sweetshop.backend.service

import com.sweetshop.backend.config.CacheConfig
import com.sweetshop.backend.dto.CategoryDto
import com.sweetshop.backend.dto.CreateCategoryRequest
import com.sweetshop.backend.dto.UpdateCategoryRequest
import com.sweetshop.backend.entity.Category
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.repository.CategoryRepository
import com.sweetshop.backend.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {
    private val logger = LoggerFactory.getLogger(CategoryService::class.java)

    @Cacheable(CacheConfig.CATEGORIES_CACHE)
    fun getCategories(): List<CategoryDto> {
        return categoryRepository.findByIsActiveTrueOrderBySortOrder().map { category ->
            val productCount = productRepository.countByCategoryId(category.id)
            category.toDto(productCount)
        }
    }

    fun getAllCategories(): List<CategoryDto> {
        return categoryRepository.findAll().sortedBy { it.sortOrder }.map { category ->
            val productCount = productRepository.countByCategoryId(category.id)
            category.toDto(productCount)
        }
    }

    fun getCategoryById(id: Long): CategoryDto {
        val category = categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }
        val productCount = productRepository.countByCategoryId(category.id)
        return category.toDto(productCount)
    }

    @Transactional
    @CacheEvict(CacheConfig.CATEGORIES_CACHE, allEntries = true)
    fun createCategory(request: CreateCategoryRequest): CategoryDto {
        val category = Category(
            name = request.name,
            description = request.description,
            image = request.image
        )
        val savedCategory = categoryRepository.save(category)
        logger.info("Category created: id={}, name={}", savedCategory.id, savedCategory.name)
        return savedCategory.toDto()
    }

    @Transactional
    @CacheEvict(CacheConfig.CATEGORIES_CACHE, allEntries = true)
    fun updateCategory(id: Long, request: UpdateCategoryRequest): CategoryDto {
        val category = categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }

        request.name?.let { category.name = it }
        request.description?.let { category.description = it }
        request.image?.let { category.image = it }
        request.isActive?.let { category.isActive = it }
        request.sortOrder?.let { category.sortOrder = it }

        val savedCategory = categoryRepository.save(category)
        logger.info("Category updated: id={}, name={}", savedCategory.id, savedCategory.name)
        val productCount = productRepository.countByCategoryId(savedCategory.id)
        return savedCategory.toDto(productCount)
    }

    @Transactional
    @CacheEvict(CacheConfig.CATEGORIES_CACHE, allEntries = true)
    fun deleteCategory(id: Long) {
        val category = categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }
        categoryRepository.delete(category)
        logger.info("Category deleted: id={}", id)
    }
}
