package com.sweetshop.admin.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.OrderDto
import com.sweetshop.admin.data.dto.OrderListDto
import com.sweetshop.admin.domain.repository.OrderRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderListState(
    val isLoading: Boolean = false,
    val orders: List<OrderListDto> = emptyList(),
    val selectedStatus: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false,
    val error: String? = null
)

data class OrderDetailState(
    val isLoading: Boolean = false,
    val order: OrderDto? = null,
    val error: String? = null,
    val isUpdatingStatus: Boolean = false,
    val statusUpdateSuccess: Boolean = false
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(OrderListState())
    val listState: StateFlow<OrderListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(OrderDetailState())
    val detailState: StateFlow<OrderDetailState> = _detailState.asStateFlow()

    companion object {
        val ORDER_STATUSES = listOf(
            null to "All",
            "PLACED" to "Placed",
            "CONFIRMED" to "Confirmed",
            "PREPARING" to "Preparing",
            "OUT_FOR_DELIVERY" to "Out for Delivery",
            "DELIVERED" to "Delivered",
            "CANCELLED" to "Cancelled"
        )

        fun getNextStatus(currentStatus: String): Pair<String, String>? {
            return when (currentStatus.uppercase()) {
                "PLACED" -> "CONFIRMED" to "Confirm Order"
                "CONFIRMED" -> "PREPARING" to "Start Preparing"
                "PREPARING" -> "OUT_FOR_DELIVERY" to "Out for Delivery"
                "OUT_FOR_DELIVERY" -> "DELIVERED" to "Mark Delivered"
                else -> null
            }
        }
    }

    init {
        loadOrders()
    }

    fun loadOrders(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            val status = _listState.value.selectedStatus
            when (val result = orderRepository.getOrders(status, page, 20)) {
                is Resource.Success -> {
                    val data = result.data
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            orders = if (page == 0) data.content else it.orders + data.content,
                            currentPage = data.page,
                            totalPages = data.totalPages,
                            hasNext = data.hasNext
                        )
                    }
                }
                is Resource.Error -> {
                    _listState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onStatusFilter(status: String?) {
        _listState.update { it.copy(selectedStatus = status) }
        loadOrders(0)
    }

    fun loadNextPage() {
        if (_listState.value.hasNext && !_listState.value.isLoading) {
            loadOrders(_listState.value.currentPage + 1)
        }
    }

    fun loadOrderDetail(orderId: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            when (val result = orderRepository.getOrder(orderId)) {
                is Resource.Success -> {
                    _detailState.update { it.copy(isLoading = false, order = result.data) }
                }
                is Resource.Error -> {
                    _detailState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isUpdatingStatus = true) }
            when (val result = orderRepository.updateOrderStatus(orderId, newStatus)) {
                is Resource.Success -> {
                    _detailState.update {
                        it.copy(
                            isUpdatingStatus = false,
                            order = result.data,
                            statusUpdateSuccess = true
                        )
                    }
                }
                is Resource.Error -> {
                    _detailState.update {
                        it.copy(isUpdatingStatus = false, error = result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearStatusUpdateSuccess() {
        _detailState.update { it.copy(statusUpdateSuccess = false) }
    }
}
