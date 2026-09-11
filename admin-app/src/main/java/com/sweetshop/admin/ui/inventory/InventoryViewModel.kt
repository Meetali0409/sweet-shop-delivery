package com.sweetshop.admin.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.InventoryItemDto
import com.sweetshop.admin.domain.repository.InventoryRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryState(
    val isLoading: Boolean = false,
    val items: List<InventoryItemDto> = emptyList(),
    val filteredItems: List<InventoryItemDto> = emptyList(),
    val selectedFilter: String = "ALL",
    val stockUpdates: Map<Long, String> = emptyMap(),
    val updatingProductId: Long? = null,
    val error: String? = null
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = inventoryRepository.getInventory()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            items = result.data,
                            filteredItems = filterItems(result.data, it.selectedFilter)
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onFilterChange(filter: String) {
        _state.update {
            it.copy(
                selectedFilter = filter,
                filteredItems = filterItems(it.items, filter)
            )
        }
    }

    fun onStockInputChange(productId: Long, value: String) {
        _state.update {
            it.copy(stockUpdates = it.stockUpdates + (productId to value))
        }
    }

    fun updateStock(productId: Long) {
        val quantity = _state.value.stockUpdates[productId]?.toIntOrNull() ?: return

        viewModelScope.launch {
            _state.update { it.copy(updatingProductId = productId) }
            when (inventoryRepository.updateStock(productId, quantity)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            updatingProductId = null,
                            stockUpdates = it.stockUpdates - productId
                        )
                    }
                    loadInventory()
                }
                is Resource.Error -> {
                    _state.update { it.copy(updatingProductId = null) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun filterItems(items: List<InventoryItemDto>, filter: String): List<InventoryItemDto> {
        return when (filter) {
            "IN_STOCK" -> items.filter { it.status.equals("IN_STOCK", ignoreCase = true) }
            "LOW_STOCK" -> items.filter { it.status.equals("LOW_STOCK", ignoreCase = true) }
            "OUT_OF_STOCK" -> items.filter { it.status.equals("OUT_OF_STOCK", ignoreCase = true) }
            else -> items
        }
    }
}
