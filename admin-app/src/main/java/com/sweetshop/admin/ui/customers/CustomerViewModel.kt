package com.sweetshop.admin.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.CustomerListDto
import com.sweetshop.admin.domain.repository.CustomerRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerState(
    val isLoading: Boolean = false,
    val customers: List<CustomerListDto> = emptyList(),
    val searchQuery: String = "",
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerState())
    val state: StateFlow<CustomerState> = _state.asStateFlow()

    init {
        loadCustomers()
    }

    fun loadCustomers(page: Int = 0) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val search = _state.value.searchQuery.ifBlank { null }
            when (val result = customerRepository.getCustomers(page, 20, search)) {
                is Resource.Success -> {
                    val data = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            customers = if (page == 0) data.content else it.customers + data.content,
                            currentPage = data.page,
                            totalPages = data.totalPages,
                            hasNext = data.hasNext
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

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        loadCustomers(0)
    }

    fun loadNextPage() {
        if (_state.value.hasNext && !_state.value.isLoading) {
            loadCustomers(_state.value.currentPage + 1)
        }
    }

    fun toggleCustomerStatus(customerId: Long) {
        viewModelScope.launch {
            when (customerRepository.toggleCustomerStatus(customerId)) {
                is Resource.Success -> {
                    loadCustomers(0)
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}
