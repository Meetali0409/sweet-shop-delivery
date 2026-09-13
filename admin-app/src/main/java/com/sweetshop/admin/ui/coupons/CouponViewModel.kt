package com.sweetshop.admin.ui.coupons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.CouponDto
import com.sweetshop.admin.data.dto.CreateCouponRequest
import com.sweetshop.admin.data.dto.UpdateCouponRequest
import com.sweetshop.admin.domain.repository.CouponRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CouponListState(
    val isLoading: Boolean = false,
    val coupons: List<CouponDto> = emptyList(),
    val error: String? = null
)

data class CouponFormState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val coupon: CouponDto? = null,
    val couponCode: String = "",
    val description: String = "",
    val discountType: String = "PERCENTAGE",
    val discountValue: String = "",
    val minimumOrderValue: String = "",
    val maximumDiscount: String = "",
    val validFrom: String = "",
    val validUntil: String = "",
    val usageLimit: String = "",
    val isActive: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class CouponViewModel @Inject constructor(
    private val couponRepository: CouponRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(CouponListState())
    val listState: StateFlow<CouponListState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(CouponFormState())
    val formState: StateFlow<CouponFormState> = _formState.asStateFlow()

    init {
        loadCoupons()
    }

    fun loadCoupons() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            when (val result = couponRepository.getCoupons()) {
                is Resource.Success -> {
                    _listState.update { it.copy(isLoading = false, coupons = result.data) }
                }
                is Resource.Error -> {
                    _listState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteCoupon(couponId: Long) {
        viewModelScope.launch {
            when (val result = couponRepository.deleteCoupon(couponId)) {
                is Resource.Success -> {
                    _listState.update { state ->
                        state.copy(coupons = state.coupons.filter { it.id != couponId })
                    }
                }
                is Resource.Error -> {
                    _listState.update { it.copy(error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadCoupon(couponId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            when (val result = couponRepository.getCoupons()) {
                is Resource.Success -> {
                    val coupon = result.data.find { it.id == couponId }
                    if (coupon != null) {
                        _formState.update {
                            it.copy(
                                isLoading = false,
                                coupon = coupon,
                                couponCode = coupon.couponCode,
                                description = coupon.description ?: "",
                                discountType = coupon.discountType,
                                discountValue = coupon.discountValue.toString(),
                                minimumOrderValue = coupon.minimumOrderValue?.toString() ?: "",
                                maximumDiscount = coupon.maximumDiscount?.toString() ?: "",
                                validFrom = coupon.validFrom ?: "",
                                validUntil = coupon.validUntil ?: "",
                                usageLimit = coupon.usageLimit?.toString() ?: "",
                                isActive = coupon.isActive
                            )
                        }
                    } else {
                        _formState.update { it.copy(isLoading = false, error = "Coupon not found") }
                    }
                }
                is Resource.Error -> {
                    _formState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onFormFieldChange(field: String, value: Any) {
        _formState.update { state ->
            when (field) {
                "couponCode" -> state.copy(couponCode = value as String)
                "description" -> state.copy(description = value as String)
                "discountType" -> state.copy(discountType = value as String)
                "discountValue" -> state.copy(discountValue = value as String)
                "minimumOrderValue" -> state.copy(minimumOrderValue = value as String)
                "maximumDiscount" -> state.copy(maximumDiscount = value as String)
                "validFrom" -> state.copy(validFrom = value as String)
                "validUntil" -> state.copy(validUntil = value as String)
                "usageLimit" -> state.copy(usageLimit = value as String)
                "isActive" -> state.copy(isActive = value as Boolean)
                else -> state
            }
        }
    }

    fun saveCoupon() {
        val state = _formState.value
        if (state.couponCode.isBlank()) {
            _formState.update { it.copy(error = "Coupon code is required") }
            return
        }
        if (state.discountValue.toDoubleOrNull() == null) {
            _formState.update { it.copy(error = "Valid discount value is required") }
            return
        }
        if (state.minimumOrderValue.toDoubleOrNull() == null) {
            _formState.update { it.copy(error = "Valid minimum order value is required") }
            return
        }
        if (state.validFrom.isBlank() || state.validUntil.isBlank()) {
            _formState.update { it.copy(error = "Valid date range is required") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, error = null) }

            val result = if (state.coupon != null) {
                couponRepository.updateCoupon(
                    state.coupon.id,
                    UpdateCouponRequest(
                        description = state.description.ifBlank { null },
                        discountValue = state.discountValue.toDoubleOrNull(),
                        minimumOrderValue = state.minimumOrderValue.toDoubleOrNull(),
                        maximumDiscount = state.maximumDiscount.toDoubleOrNull(),
                        validUntil = state.validUntil.ifBlank { null },
                        usageLimit = state.usageLimit.toIntOrNull(),
                        isActive = state.isActive
                    )
                )
            } else {
                couponRepository.createCoupon(
                    CreateCouponRequest(
                        couponCode = state.couponCode.uppercase(),
                        description = state.description.ifBlank { null },
                        discountType = state.discountType,
                        discountValue = state.discountValue.toDouble(),
                        minimumOrderValue = state.minimumOrderValue.toDouble(),
                        maximumDiscount = state.maximumDiscount.toDoubleOrNull(),
                        validFrom = state.validFrom,
                        validUntil = state.validUntil,
                        usageLimit = state.usageLimit.toIntOrNull(),
                        isActive = state.isActive
                    )
                )
            }

            when (result) {
                is Resource.Success -> {
                    _formState.update { it.copy(isSaving = false, isSaved = true) }
                }
                is Resource.Error -> {
                    _formState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun resetFormState() {
        _formState.value = CouponFormState()
    }
}
