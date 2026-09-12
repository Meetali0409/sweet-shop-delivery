package com.sweetshop.customer.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.domain.repository.WishlistRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WishlistUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    init {
        loadWishlist()
    }

    fun loadWishlist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = wishlistRepository.getWishlist()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(products = result.data, isLoading = false)
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

    fun removeFromWishlist(productId: Long) {
        viewModelScope.launch {
            when (wishlistRepository.removeFromWishlist(productId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(products = it.products.filter { p -> p.id != productId })
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}
