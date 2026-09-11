package com.sweetshop.customer.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

enum class SortOption(val displayName: String, val apiValue: String) {
    PRICE_LOW_HIGH("Price: Low to High", "price,asc"),
    PRICE_HIGH_LOW("Price: High to Low", "price,desc"),
    RATING("Rating", "rating,desc"),
    NEWEST("Newest", "createdAt,desc")
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NEWEST,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val categoryId: Long? = null,
    val addToCartMessage: String? = null
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        val categoryId = savedStateHandle.get<Long>("categoryId")
        _uiState.update { it.copy(categoryId = categoryId) }
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = 0) }
            val state = _uiState.value
            when (val result = productRepository.getProducts(
                categoryId = state.categoryId,
                page = 0,
                sort = state.sortOption.apiValue,
                search = state.searchQuery.ifBlank { null }
            )) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            products = result.data,
                            isLoading = false,
                            hasMore = result.data.size >= 20
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = result.message, isLoading = false)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = state.currentPage + 1
            when (val result = productRepository.getProducts(
                categoryId = state.categoryId,
                page = nextPage,
                sort = state.sortOption.apiValue,
                search = state.searchQuery.ifBlank { null }
            )) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            products = it.products + result.data,
                            currentPage = nextPage,
                            isLoadingMore = false,
                            hasMore = result.data.size >= 20
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadProducts()
    }

    fun onSortOptionChange(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
        loadProducts()
    }

    fun addToCart(productId: Long) {
        viewModelScope.launch {
            when (val result = cartRepository.addToCart(productId, null, 1)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(addToCartMessage = "Added to cart") }
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
