package com.sweetshop.admin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.DashboardDto
import com.sweetshop.admin.data.dto.SalesReportDto
import com.sweetshop.admin.domain.repository.ReportRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isLoading: Boolean = false,
    val dashboard: DashboardDto? = null,
    val salesOverview: List<SalesReportDto> = emptyList(),
    val selectedPeriod: String = "weekly",
    val error: String? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
        loadSalesOverview("weekly")
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = reportRepository.getDashboard()) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, dashboard = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadSalesOverview(period: String) {
        _state.update { it.copy(selectedPeriod = period) }
        viewModelScope.launch {
            when (val result = reportRepository.getSalesOverview(period)) {
                is Resource.Success -> {
                    _state.update { it.copy(salesOverview = result.data) }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            when (val result = reportRepository.getDashboard()) {
                is Resource.Success -> {
                    _state.update { it.copy(isRefreshing = false, dashboard = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isRefreshing = false) }
                }
                is Resource.Loading -> {}
            }
            loadSalesOverview(_state.value.selectedPeriod)
        }
    }
}
