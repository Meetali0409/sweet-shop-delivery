package com.sweetshop.customer.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.data.api.SweetShopApi
import com.sweetshop.customer.data.dto.CreatePaymentOrderRequest
import com.sweetshop.customer.data.dto.VerifyPaymentRequest
import com.sweetshop.customer.domain.model.Address
import com.sweetshop.customer.domain.model.Cart
import com.sweetshop.customer.domain.model.Order
import com.sweetshop.customer.domain.repository.AddressRepository
import com.sweetshop.customer.domain.repository.CartRepository
import com.sweetshop.customer.domain.repository.OrderRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val cart: Cart = Cart(),
    val paymentMethod: String = "COD",
    val availablePaymentMethods: List<String> = listOf("COD"),
    val orderNotes: String = "",
    val isLoading: Boolean = true,
    val isPlacingOrder: Boolean = false,
    val error: String? = null,
    val placedOrder: Order? = null,
    val showAddressForm: Boolean = false,
    val newAddress: Address = Address(),
    val isSavingAddress: Boolean = false,
    val pendingPaymentOrderId: Long? = null,
    val razorpayOrderId: String? = null,
    val razorpayKeyId: String? = null,
    val razorpayAmount: Long? = null,
    val razorpayCurrency: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val api: SweetShopApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load addresses
            launch {
                when (val result = addressRepository.getAddresses()) {
                    is Resource.Success -> {
                        val defaultAddr = result.data.firstOrNull { it.isDefault } ?: result.data.firstOrNull()
                        _uiState.update {
                            it.copy(addresses = result.data, selectedAddress = defaultAddr)
                        }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            // Load cart
            launch {
                when (val result = cartRepository.getCart()) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(cart = result.data) }
                    }
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }

            // Load available payment methods from config
            launch {
                try {
                    val response = api.getShopConfig()
                    if (response.isSuccessful) {
                        val config = response.body()?.data
                        if (config != null) {
                            _uiState.update {
                                it.copy(availablePaymentMethods = config.paymentMethods)
                            }
                        }
                    }
                } catch (_: Exception) { }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectAddress(address: Address) {
        _uiState.update { it.copy(selectedAddress = address) }
    }

    fun selectPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun updateOrderNotes(notes: String) {
        _uiState.update { it.copy(orderNotes = notes) }
    }

    fun toggleAddressForm() {
        _uiState.update { it.copy(showAddressForm = !it.showAddressForm, newAddress = Address()) }
    }

    fun updateNewAddress(address: Address) {
        _uiState.update { it.copy(newAddress = address) }
    }

    fun saveNewAddress() {
        val address = _uiState.value.newAddress
        if (address.fullName.isBlank() || address.phone.isBlank() ||
            address.addressLine1.isBlank() || address.city.isBlank() ||
            address.state.isBlank() || address.pincode.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all required fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingAddress = true) }
            when (val result = addressRepository.createAddress(address)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            addresses = it.addresses + result.data,
                            selectedAddress = result.data,
                            showAddressForm = false,
                            isSavingAddress = false,
                            newAddress = Address()
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = result.message, isSavingAddress = false)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            when (addressRepository.deleteAddress(addressId)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        val updatedAddresses = state.addresses.filter { it.id != addressId }
                        state.copy(
                            addresses = updatedAddresses,
                            selectedAddress = if (state.selectedAddress?.id == addressId)
                                updatedAddresses.firstOrNull() else state.selectedAddress
                        )
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun placeOrder() {
        val state = _uiState.value
        if (state.selectedAddress == null) {
            _uiState.update { it.copy(error = "Please select a delivery address") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingOrder = true, error = null) }
            when (val result = orderRepository.createOrder(
                addressId = state.selectedAddress.id,
                paymentMethod = state.paymentMethod,
                notes = state.orderNotes.ifBlank { null },
                couponCode = state.cart.couponCode
            )) {
                is Resource.Success -> {
                    if (state.paymentMethod == "ONLINE") {
                        initiateOnlinePayment(result.data.id)
                    } else {
                        _uiState.update {
                            it.copy(placedOrder = result.data, isPlacingOrder = false)
                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = result.message, isPlacingOrder = false)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private suspend fun initiateOnlinePayment(orderId: Long) {
        try {
            val response = api.createPaymentOrder(CreatePaymentOrderRequest(orderId = orderId))
            if (response.isSuccessful) {
                val paymentOrder = response.body()?.data
                if (paymentOrder != null) {
                    _uiState.update {
                        it.copy(
                            pendingPaymentOrderId = orderId,
                            razorpayOrderId = paymentOrder.razorpayOrderId,
                            razorpayKeyId = paymentOrder.keyId,
                            razorpayAmount = paymentOrder.amount,
                            razorpayCurrency = paymentOrder.currency,
                            isPlacingOrder = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(error = "Failed to create payment order", isPlacingOrder = false) }
                }
            } else {
                _uiState.update { it.copy(error = "Failed to create payment order", isPlacingOrder = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Payment initiation failed: ${e.message}", isPlacingOrder = false) }
        }
    }

    fun onPaymentSuccess(razorpayPaymentId: String, razorpaySignature: String) {
        val state = _uiState.value
        val orderId = state.pendingPaymentOrderId ?: return
        val razorpayOrderId = state.razorpayOrderId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingOrder = true) }
            try {
                val response = api.verifyPayment(
                    VerifyPaymentRequest(
                        orderId = orderId,
                        razorpayOrderId = razorpayOrderId,
                        razorpayPaymentId = razorpayPaymentId,
                        razorpaySignature = razorpaySignature
                    )
                )
                if (response.isSuccessful && response.body()?.data?.success == true) {
                    // Fetch the order to show confirmation
                    when (val orderResult = orderRepository.getOrderById(orderId)) {
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    placedOrder = orderResult.data,
                                    isPlacingOrder = false,
                                    pendingPaymentOrderId = null,
                                    razorpayOrderId = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(error = "Payment verified but could not load order", isPlacingOrder = false) }
                        }
                        is Resource.Loading -> {}
                    }
                } else {
                    _uiState.update { it.copy(error = "Payment verification failed", isPlacingOrder = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Payment verification error: ${e.message}", isPlacingOrder = false) }
            }
        }
    }

    fun onPaymentError(errorMessage: String) {
        _uiState.update {
            it.copy(
                error = "Payment failed: $errorMessage",
                isPlacingOrder = false,
                pendingPaymentOrderId = null,
                razorpayOrderId = null
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
