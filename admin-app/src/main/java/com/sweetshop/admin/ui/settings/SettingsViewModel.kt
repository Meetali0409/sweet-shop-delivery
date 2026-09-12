package com.sweetshop.admin.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.UpdateShopConfigRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val shopName: String = "",
    val tagline: String = "",
    val supportEmail: String = "",
    val supportPhone: String = "",
    val taxRate: String = "",
    val deliveryCharge: String = "",
    val freeDeliveryThreshold: String = "",
    val shopLatitude: String = "",
    val shopLongitude: String = "",
    val deliveryRadiusKm: String = "",
    val perKmCharge: String = "",
    val baseDeliveryDistanceKm: String = "",
    val estimatedDeliveryDays: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val api: AdminApi
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadConfig()
    }

    fun loadConfig() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = api.getShopConfig()
                if (response.isSuccessful && response.body()?.success == true) {
                    val config = response.body()!!.data!!
                    _state.update {
                        it.copy(
                            isLoading = false,
                            shopName = config.shopName,
                            tagline = config.tagline ?: "",
                            supportEmail = config.supportEmail ?: "",
                            supportPhone = config.supportPhone ?: "",
                            taxRate = config.taxRate.toString(),
                            deliveryCharge = config.deliveryCharge?.toString() ?: "",
                            freeDeliveryThreshold = config.freeDeliveryThreshold?.toString() ?: "",
                            shopLatitude = config.shopLatitude?.toString() ?: "",
                            shopLongitude = config.shopLongitude?.toString() ?: "",
                            deliveryRadiusKm = config.deliveryRadiusKm?.toString() ?: "",
                            perKmCharge = config.perKmCharge?.toString() ?: "",
                            baseDeliveryDistanceKm = config.baseDeliveryDistanceKm?.toString() ?: "",
                            estimatedDeliveryDays = config.estimatedDeliveryDays?.toString() ?: ""
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.body()?.message ?: "Failed to load settings"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Something went wrong")
                }
            }
        }
    }

    fun onFieldChange(field: String, value: String) {
        _state.update { state ->
            when (field) {
                "shopName" -> state.copy(shopName = value)
                "tagline" -> state.copy(tagline = value)
                "supportEmail" -> state.copy(supportEmail = value)
                "supportPhone" -> state.copy(supportPhone = value)
                "taxRate" -> state.copy(taxRate = value)
                "deliveryCharge" -> state.copy(deliveryCharge = value)
                "freeDeliveryThreshold" -> state.copy(freeDeliveryThreshold = value)
                "shopLatitude" -> state.copy(shopLatitude = value)
                "shopLongitude" -> state.copy(shopLongitude = value)
                "deliveryRadiusKm" -> state.copy(deliveryRadiusKm = value)
                "perKmCharge" -> state.copy(perKmCharge = value)
                "baseDeliveryDistanceKm" -> state.copy(baseDeliveryDistanceKm = value)
                "estimatedDeliveryDays" -> state.copy(estimatedDeliveryDays = value)
                else -> state
            }
        }
    }

    fun saveSettings() {
        val current = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, successMessage = null) }
            try {
                val request = UpdateShopConfigRequest(
                    shopName = current.shopName.ifBlank { null },
                    tagline = current.tagline.ifBlank { null },
                    supportEmail = current.supportEmail.ifBlank { null },
                    supportPhone = current.supportPhone.ifBlank { null },
                    taxRate = current.taxRate.toDoubleOrNull(),
                    deliveryCharge = current.deliveryCharge.toDoubleOrNull(),
                    freeDeliveryThreshold = current.freeDeliveryThreshold.toDoubleOrNull(),
                    shopLatitude = current.shopLatitude.toDoubleOrNull(),
                    shopLongitude = current.shopLongitude.toDoubleOrNull(),
                    deliveryRadiusKm = current.deliveryRadiusKm.toDoubleOrNull(),
                    perKmCharge = current.perKmCharge.toDoubleOrNull(),
                    baseDeliveryDistanceKm = current.baseDeliveryDistanceKm.toDoubleOrNull(),
                    estimatedDeliveryDays = current.estimatedDeliveryDays.toIntOrNull()
                )

                val response = api.updateShopConfig(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _state.update {
                        it.copy(isSaving = false, successMessage = "Settings saved successfully")
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = response.body()?.message ?: "Failed to save settings"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSaving = false, error = e.message ?: "Something went wrong")
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
