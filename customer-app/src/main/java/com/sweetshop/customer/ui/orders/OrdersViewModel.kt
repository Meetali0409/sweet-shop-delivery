package com.sweetshop.customer.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.Order
import com.sweetshop.customer.domain.repository.OrderRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedTab: String = "All",
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false,
    val message: String? = null,
    val showCancelDialog: Boolean = false,
    val cancelReason: String = ""
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = 0) }
            val statusFilter = when (_uiState.value.selectedTab) {
                "All" -> null
                "Processing" -> "PLACED,CONFIRMED,PREPARING"
                "Delivered" -> "DELIVERED"
                "Cancelled" -> "CANCELLED"
                else -> null
            }

            when (val result = orderRepository.getOrders(status = statusFilter, page = 0)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            orders = result.data,
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
            when (val result = orderRepository.getOrders(page = nextPage)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            orders = it.orders + result.data,
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

    fun selectTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
        loadOrders()
    }

    fun loadOrderDetail(orderId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.getOrderById(orderId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(selectedOrder = result.data, isLoading = false)
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

    fun showCancelDialog() {
        _uiState.update { it.copy(showCancelDialog = true) }
    }

    fun hideCancelDialog() {
        _uiState.update { it.copy(showCancelDialog = false, cancelReason = "") }
    }

    fun updateCancelReason(reason: String) {
        _uiState.update { it.copy(cancelReason = reason) }
    }

    fun cancelOrder() {
        val state = _uiState.value
        val orderId = state.selectedOrder?.id ?: return
        val reason = state.cancelReason.ifBlank { "Customer requested cancellation" }

        viewModelScope.launch {
            when (val result = orderRepository.cancelOrder(orderId, reason)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            selectedOrder = result.data,
                            showCancelDialog = false,
                            cancelReason = "",
                            message = "Order cancelled successfully"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(message = result.message, showCancelDialog = false)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun reorder(orderId: Long) {
        viewModelScope.launch {
            when (val result = orderRepository.reorder(orderId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(message = "Items added to cart") }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(message = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
