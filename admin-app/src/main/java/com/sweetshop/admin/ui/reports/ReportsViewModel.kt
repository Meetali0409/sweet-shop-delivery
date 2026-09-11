package com.sweetshop.admin.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.SalesReportDto
import com.sweetshop.admin.domain.repository.ReportRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class ReportsState(
    val isLoading: Boolean = false,
    val selectedPeriod: String = "weekly",
    val salesOverview: List<SalesReportDto> = emptyList(),
    val salesReport: SalesReportDto? = null,
    val dateFrom: String = "",
    val dateTo: String = "",
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    init {
        setDefaultDates()
        loadSalesOverview("weekly")
        loadSalesReport()
    }

    private fun setDefaultDates() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val to = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val from = dateFormat.format(calendar.time)
        _state.update { it.copy(dateFrom = from, dateTo = to) }
    }

    fun onPeriodChange(period: String) {
        _state.update { it.copy(selectedPeriod = period) }
        loadSalesOverview(period)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val to = dateFormat.format(calendar.time)

        when (period) {
            "daily" -> calendar.add(Calendar.DAY_OF_YEAR, -1)
            "weekly" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            "monthly" -> calendar.add(Calendar.MONTH, -1)
        }
        val from = dateFormat.format(calendar.time)
        _state.update { it.copy(dateFrom = from, dateTo = to) }
        loadSalesReport()
    }

    fun loadSalesOverview(period: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = reportRepository.getSalesOverview(period)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, salesOverview = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadSalesReport() {
        val from = _state.value.dateFrom
        val to = _state.value.dateTo
        if (from.isBlank() || to.isBlank()) return

        viewModelScope.launch {
            when (val result = reportRepository.getSalesReport(from, to)) {
                is Resource.Success -> {
                    _state.update { it.copy(salesReport = result.data) }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun onDateFromChange(date: String) {
        _state.update { it.copy(dateFrom = date) }
    }

    fun onDateToChange(date: String) {
        _state.update { it.copy(dateTo = date) }
    }

    fun applyCustomDates() {
        loadSalesReport()
    }
}
