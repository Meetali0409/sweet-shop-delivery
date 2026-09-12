package com.sweetshop.customer.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.model.ProductVariant
import com.sweetshop.customer.domain.repository.CartRepository
import com.sweetshop.customer.domain.repository.ProductRepository
import com.sweetshop.customer.domain.repository.WishlistRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: Product? = null,
    val relatedProducts: List<Product> = emptyList(),
    val selectedVariant: ProductVariant? = null,
    val quantity: Int = 1,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isInWishlist: Boolean = false,
    val addedToCart: Boolean = false,
    val navigateToCart: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val productId: Long = savedStateHandle.get<Long>("productId") ?: 0L

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = productRepository.getProductById(productId)) {
                is Resource.Success -> {
                    val product = result.data
                    _uiState.update {
                        it.copy(
                            product = product,
                            selectedVariant = product.variants.firstOrNull(),
                            isLoading = false
                        )
                    }
                    // Load related products
                    loadRelatedProducts(product.categoryId)
                    // Check wishlist
                    checkWishlist()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadRelatedProducts(categoryId: Long) {
        viewModelScope.launch {
            when (val result = productRepository.getProducts(categoryId = categoryId, size = 10)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(relatedProducts = result.data.filter { p -> p.id != productId })
                    }
                }
                else -> {}
            }
        }
    }

    private fun checkWishlist() {
        viewModelScope.launch {
            when (val result = wishlistRepository.isInWishlist(productId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isInWishlist = result.data) }
                }
                else -> {}
            }
        }
    }

    fun selectVariant(variant: ProductVariant) {
        _uiState.update { it.copy(selectedVariant = variant) }
    }

    fun incrementQuantity() {
        _uiState.update { it.copy(quantity = (it.quantity + 1).coerceAtMost(10)) }
    }

    fun decrementQuantity() {
        _uiState.update { it.copy(quantity = (it.quantity - 1).coerceAtLeast(1)) }
    }

    fun addToCart() {
        viewModelScope.launch {
            val state = _uiState.value
            val selectedWeight = state.selectedVariant?.weight
            when (val result = cartRepository.addToCart(productId, selectedWeight, state.quantity)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(addedToCart = true, message = "Added to cart") }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(message = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun buyNow() {
        viewModelScope.launch {
            val state = _uiState.value
            val selectedWeight = state.selectedVariant?.weight
            when (val result = cartRepository.addToCart(productId, selectedWeight, state.quantity)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(addedToCart = true, navigateToCart = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(message = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearNavigateToCart() {
        _uiState.update { it.copy(navigateToCart = false, addedToCart = false) }
    }

    fun toggleWishlist() {
        viewModelScope.launch {
            val isInWishlist = _uiState.value.isInWishlist
            if (isInWishlist) {
                when (wishlistRepository.removeFromWishlist(productId)) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isInWishlist = false, message = "Removed from wishlist") }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            } else {
                when (wishlistRepository.addToWishlist(productId)) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(isInWishlist = true, message = "Added to wishlist") }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, addedToCart = false) }
    }
}
