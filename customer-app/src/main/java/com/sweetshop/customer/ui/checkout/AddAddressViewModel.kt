package com.sweetshop.customer.ui.checkout

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sweetshop.customer.domain.model.Address
import com.sweetshop.customer.domain.repository.AddressRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

data class AddAddressState(
    val fullName: String = "",
    val phone: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val addressType: String = "HOME",
    val isDefault: Boolean = false,
    val isDetectingLocation: Boolean = false,
    val locationDetected: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AddAddressState())
    val state: StateFlow<AddAddressState> = _state.asStateFlow()

    fun onFieldChange(field: String, value: Any) {
        _state.update {
            when (field) {
                "fullName" -> it.copy(fullName = value as String)
                "phone" -> it.copy(phone = value as String)
                "addressLine1" -> it.copy(addressLine1 = value as String)
                "addressLine2" -> it.copy(addressLine2 = value as String)
                "city" -> it.copy(city = value as String)
                "state" -> it.copy(state = value as String)
                "pincode" -> it.copy(pincode = value as String)
                "landmark" -> it.copy(landmark = value as String)
                "isDefault" -> it.copy(isDefault = value as Boolean)
                else -> it
            }
        }
    }

    fun selectAddressType(type: String) {
        _state.update { it.copy(addressType = type) }
    }

    fun detectCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isDetectingLocation = true, error = null) }
            try {
                val locationClient = LocationServices.getFusedLocationProviderClient(appContext)
                val location = locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()

                if (location != null) {
                    _state.update { it.copy(latitude = location.latitude, longitude = location.longitude) }
                    reverseGeocode(location.latitude, location.longitude)
                } else {
                    _state.update {
                        it.copy(
                            isDetectingLocation = false,
                            error = "Could not detect location. Please enter address manually."
                        )
                    }
                }
            } catch (e: SecurityException) {
                _state.update { it.copy(isDetectingLocation = false, error = "Location permission not granted") }
            } catch (e: Exception) {
                _state.update { it.copy(isDetectingLocation = false, error = "Location detection failed: ${e.message}") }
            }
        }
    }

    private fun reverseGeocode(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(appContext, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                _state.update {
                    it.copy(
                        isDetectingLocation = false,
                        locationDetected = true,
                        addressLine1 = addr.subThoroughfare?.let { num ->
                            "$num, ${addr.thoroughfare ?: ""}"
                        } ?: addr.thoroughfare ?: addr.featureName ?: "",
                        addressLine2 = addr.subLocality ?: addr.locality ?: "",
                        city = addr.locality ?: addr.subAdminArea ?: "",
                        state = addr.adminArea ?: "",
                        pincode = addr.postalCode ?: "",
                    )
                }
            } else {
                _state.update { it.copy(isDetectingLocation = false, locationDetected = true) }
            }
        } catch (e: Exception) {
            _state.update { it.copy(isDetectingLocation = false, locationDetected = true) }
        }
    }

    fun saveAddress() {
        val current = _state.value

        if (current.fullName.isBlank() || current.phone.isBlank() || current.addressLine1.isBlank() ||
            current.city.isBlank() || current.state.isBlank() || current.pincode.isBlank()
        ) {
            _state.update { it.copy(error = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val address = Address(
                fullName = current.fullName,
                phone = current.phone,
                addressLine1 = current.addressLine1,
                addressLine2 = current.addressLine2,
                city = current.city,
                state = current.state,
                pincode = current.pincode,
                landmark = current.landmark,
                latitude = current.latitude,
                longitude = current.longitude,
                isDefault = current.isDefault,
                type = current.addressType
            )

            when (val result = addressRepository.createAddress(address)) {
                is Resource.Success -> _state.update { it.copy(isSaving = false, isSaved = true) }
                is Resource.Error -> _state.update { it.copy(isSaving = false, error = result.message) }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
