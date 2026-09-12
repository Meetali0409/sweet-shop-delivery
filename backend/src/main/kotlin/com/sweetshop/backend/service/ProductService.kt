package com.sweetshop.backend.service

import com.sweetshop.backend.dto.*
import com.sweetshop.backend.entity.ProductWeight
import com.sweetshop.backend.exception.BadRequestException
import com.sweetshop.backend.exception.ResourceNotFoundException
import com.sweetshop.backend.mapper.toDto
import com.sweetshop.backend.mapper.toEntity
import com.sweetshop.backend.mapper.toListDto
import com.sweetshop.backend.repository.CategoryRepository
import com.sweetshop.backend.repository.ProductRepository
import com.sweetshop.backend.repository.WishlistItemRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val wishlistItemRepository: WishlistItemRepository
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    fun getProducts(
        categoryId: Long?,
        search: String?,
        sort: String?,
        pageable: Pageable
    ): PagedResponse<ProductListDto> {
        val page = when {
            !search.isNullOrBlank() -> productRepository.searchProducts(search, pageable)
            categoryId != null -> productRepository.findByCategoryIdAndIsAvailableTrue(categoryId, pageable)
            else -> productRepository.findByIsAvailableTrue(pageable)
        }

        return PagedResponse(
            content = page.content.map { it.toListDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    fun getProductById(id: Long, userId: Long?): ProductDto {
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }

        val isInWishlist = userId?.let {
            wishlistItemRepository.existsByUserIdAndProductId(it, id)
        } ?: false

        return product.toDto(isInWishlist)
    }

    fun getFeaturedProducts(pageable: Pageable): PagedResponse<ProductListDto> {
        val page = productRepository.findByIsFeaturedTrueAndIsAvailableTrue(pageable)
        return PagedResponse(
            content = page.content.map { it.toListDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    fun getBestsellerProducts(pageable: Pageable): PagedResponse<ProductListDto> {
        val page = productRepository.findByIsBestsellerTrueAndIsAvailableTrue(pageable)
        return PagedResponse(
            content = page.content.map { it.toListDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext()
        )
    }

    @Transactional
    fun createProduct(request: CreateProductRequest): ProductDto {
        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { ResourceNotFoundException("Category not found with id: ${request.categoryId}") }

        val product = request.toEntity(category)

        request.weights?.forEach { weightReq ->
            val weight = ProductWeight(
                product = product,
                weight = weightReq.weight,
                price = weightReq.price,
                discountPrice = weightReq.discountPrice
            )
            product.weights.add(weight)
        }

        val savedProduct = productRepository.save(product)
        logger.info("Product created: id={}, name={}", savedProduct.id, savedProduct.name)
        return savedProduct.toDto()
    }

    @Transactional
    fun updateProduct(id: Long, request: UpdateProductRequest): ProductDto {
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }

        request.name?.let { product.name = it }
        request.description?.let { product.description = it }
        request.categoryId?.let { catId ->
            val category = categoryRepository.findById(catId)
                .orElseThrow { ResourceNotFoundException("Category not found with id: $catId") }
            product.category = category
        }
        request.imageUrl?.let { product.imageUrl = it }
        request.price?.let { product.price = it }
        request.discountPrice?.let { product.discountPrice = it }
        request.unit?.let { product.unit = it }
        request.stockQuantity?.let { product.stockQuantity = it }
        request.minimumOrderQuantity?.let { product.minimumOrderQuantity = it }
        request.isAvailable?.let { product.isAvailable = it }
        request.isFeatured?.let { product.isFeatured = it }
        request.isBestseller?.let { product.isBestseller = it }
        request.ingredients?.let { product.ingredients = it }
        request.allergenInfo?.let { product.allergenInfo = it }

        request.weights?.let { weightRequests ->
            product.weights.clear()
            weightRequests.forEach { weightReq ->
                val weight = ProductWeight(
                    product = product,
                    weight = weightReq.weight,
                    price = weightReq.price,
                    discountPrice = weightReq.discountPrice
                )
                product.weights.add(weight)
            }
        }

        val savedProduct = productRepository.save(product)
        logger.info("Product updated: id={}, name={}", savedProduct.id, savedProduct.name)
        return savedProduct.toDto()
    }

    @Transactional
    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }
        productRepository.delete(product)
        logger.info("Product deleted: id={}", id)
    }

    @Transactional
    fun updateStock(id: Long, quantity: Int): ProductDto {
        if (quantity < 0) {
            throw BadRequestException("Stock quantity cannot be negative")
        }

        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }

        product.stockQuantity = quantity
        product.isAvailable = quantity > 0

        val savedProduct = productRepository.save(product)
        logger.info("Stock updated for product: id={}, newStock={}", id, quantity)
        return savedProduct.toDto()
    }

    fun getLowStockProducts(threshold: Int): List<InventoryItemDto> {
        val products = productRepository.findByStockQuantityLessThanAndIsAvailableTrue(threshold)
        return products.map { product ->
            InventoryItemDto(
                productId = product.id,
                productName = product.name,
                currentStock = product.stockQuantity,
                status = when {
                    product.stockQuantity <= 0 -> StockStatus.OUT_OF_STOCK
                    product.stockQuantity < threshold -> StockStatus.LOW_STOCK
                    else -> StockStatus.IN_STOCK
                }
            )
        }
    }

    fun getAllInventory(lowStockThreshold: Int): List<InventoryItemDto> {
        val products = productRepository.findAll()
        return products.map { product ->
            InventoryItemDto(
                productId = product.id,
                productName = product.name,
                currentStock = product.stockQuantity,
                status = when {
                    product.stockQuantity <= 0 -> StockStatus.OUT_OF_STOCK
                    product.stockQuantity < lowStockThreshold -> StockStatus.LOW_STOCK
                    else -> StockStatus.IN_STOCK
                }
            )
        }
    }
}
