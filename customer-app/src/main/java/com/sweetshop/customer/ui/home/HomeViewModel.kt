package com.sweetshop.customer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.Category
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.repository.CartRepository
import com.sweetshop.customer.domain.repository.ProductRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val bestsellers: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList(),
    val searchResults: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val cartItemCount: Int = 0,
    val isSearching: Boolean = false,
    val addToCartMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load categories
            launch {
                when (val result = productRepository.getCategories()) {
                    is Resource.Success -> _uiState.update { it.copy(categories = result.data) }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            // Load featured
            launch {
                when (val result = productRepository.getFeaturedProducts()) {
                    is Resource.Success -> _uiState.update { it.copy(featuredProducts = result.data) }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            // Load bestsellers
            launch {
                when (val result = productRepository.getBestsellers()) {
                    is Resource.Success -> _uiState.update { it.copy(bestsellers = result.data) }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            // Load new arrivals
            launch {
                when (val result = productRepository.getNewArrivals()) {
                    is Resource.Success -> _uiState.update { it.copy(newArrivals = result.data) }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            // Load cart count
            launch {
                when (val result = cartRepository.getCartItemCount()) {
                    is Resource.Success -> _uiState.update { it.copy(cartItemCount = result.data) }
                    else -> {}
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(isSearching = false, searchResults = emptyList()) }
            return
        }
        _uiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            when (val result = productRepository.searchProducts(query)) {
                is Resource.Success -> _uiState.update {
                    it.copy(searchResults = result.data, isSearching = false)
                }
                is Resource.Error -> _uiState.update { it.copy(isSearching = false) }
                is Resource.Loading -> {}
            }
        }
    }

    fun addToCart(productId: Long) {
        viewModelScope.launch {
            when (val result = cartRepository.addToCart(productId, null, 1)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            cartItemCount = result.data.itemCount,
                            addToCartMessage = "Added to cart"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(addToCartMessage = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearAddToCartMessage() {
        _uiState.update { it.copy(addToCartMessage = null) }
    }
}
