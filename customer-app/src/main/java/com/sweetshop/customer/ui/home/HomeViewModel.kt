package com.sweetshop.customer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.Category
import com.sweetshop.customer.domain.model.CartItem
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.repository.CartRepository
import com.sweetshop.customer.domain.repository.ProductRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
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
    val cartItemsByProductId: Map<Long, CartItem> = emptyMap(),
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

    private var searchJob: Job? = null

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val jobs = listOf(
                // Load categories
                launch {
                    when (val result = productRepository.getCategories()) {
                        is Resource.Success -> _uiState.update { it.copy(categories = result.data) }
                        is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                        is Resource.Loading -> {}
                    }
                },

                // Load featured
                launch {
                    when (val result = productRepository.getFeaturedProducts()) {
                        is Resource.Success -> _uiState.update { it.copy(featuredProducts = result.data) }
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                    }
                },

                // Load bestsellers
                launch {
                    when (val result = productRepository.getBestsellers()) {
                        is Resource.Success -> _uiState.update { it.copy(bestsellers = result.data) }
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                    }
                },

                // Load new arrivals
                launch {
                    when (val result = productRepository.getNewArrivals()) {
                        is Resource.Success -> _uiState.update { it.copy(newArrivals = result.data) }
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                    }
                },

                // Load cart
                launch {
                    when (val result = cartRepository.getCart()) {
                        is Resource.Success -> _uiState.update {
                            it.copy(
                                cartItemCount = result.data.itemCount,
                                cartItemsByProductId = buildCartMap(result.data.items)
                            )
                        }
                        else -> {}
                    }
                }
            )

            jobs.joinAll()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun buildCartMap(items: List<CartItem>): Map<Long, CartItem> {
        return items
            .groupBy { it.productId }
            .mapValues { (_, itemsForProduct) ->
                itemsForProduct.firstOrNull { it.variantId == null } ?: itemsForProduct.first()
            }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            searchJob?.cancel()
            _uiState.update { it.copy(isSearching = false, searchResults = emptyList()) }
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce
            _uiState.update { it.copy(isSearching = true) }
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
                            cartItemsByProductId = buildCartMap(result.data.items),
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

    fun incrementCartItem(productId: Long) {
        val existing = _uiState.value.cartItemsByProductId[productId]
        if (existing == null) {
            addToCart(productId)
            return
        }
        viewModelScope.launch {
            when (val result = cartRepository.updateCartItem(existing.id, existing.quantity + 1)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            cartItemCount = result.data.itemCount,
                            cartItemsByProductId = buildCartMap(result.data.items)
                        )
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(addToCartMessage = result.message) }
                is Resource.Loading -> {}
            }
        }
    }

    fun decrementCartItem(productId: Long) {
        val existing = _uiState.value.cartItemsByProductId[productId] ?: return
        viewModelScope.launch {
            val newQuantity = existing.quantity - 1
            val result = if (newQuantity < 1) {
                cartRepository.removeCartItem(existing.id)
            } else {
                cartRepository.updateCartItem(existing.id, newQuantity)
            }
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            cartItemCount = result.data.itemCount,
                            cartItemsByProductId = buildCartMap(result.data.items)
                        )
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(addToCartMessage = result.message) }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearAddToCartMessage() {
        _uiState.update { it.copy(addToCartMessage = null) }
    }
}
