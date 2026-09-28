package com.sweetshop.customer.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartBadgeViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val cartItemCount: StateFlow<Int> = cartRepository.cartItemCount

    init {
        viewModelScope.launch {
            cartRepository.getCartItemCount()
        }
    }
}
