package com.sweetshop.customer.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.Cart
import com.sweetshop.customer.domain.repository.CartRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cart: Cart = Cart(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val couponCode: String = "",
    val couponMessage: String? = null,
    val isApplyingCoupon: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = cartRepository.getCart()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(cart = result.data, isLoading = false)
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

    fun updateQuantity(itemId: Long, newQuantity: Int) {
        if (newQuantity < 1) {
            removeItem(itemId)
            return
        }
        viewModelScope.launch {
            when (val result = cartRepository.updateCartItem(itemId, newQuantity)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(cart = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(message = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun removeItem(itemId: Long) {
        viewModelScope.launch {
            when (val result = cartRepository.removeCartItem(itemId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(cart = result.data, message = "Item removed") }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(message = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            when (cartRepository.clearCart()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(cart = Cart(), message = "Cart cleared") }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(message = "Failed to clear cart") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateCouponCode(code: String) {
        _uiState.update { it.copy(couponCode = code, couponMessage = null) }
    }

    fun applyCoupon() {
        val code = _uiState.value.couponCode.trim()
        if (code.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isApplyingCoupon = true, couponMessage = null) }
            when (val result = cartRepository.applyCoupon(code)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            cart = result.data,
                            isApplyingCoupon = false,
                            couponMessage = "Coupon applied successfully!"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isApplyingCoupon = false, couponMessage = result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun removeCoupon() {
        viewModelScope.launch {
            when (val result = cartRepository.removeCoupon()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(cart = result.data, couponCode = "", couponMessage = "Coupon removed")
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
