package com.sweetshop.customer.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.customer.domain.model.User
import com.sweetshop.customer.domain.repository.AuthRepository
import com.sweetshop.customer.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val user: User? = null
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    suspend fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun updateLoginEmail(email: String) {
        _loginState.update { it.copy(email = email, error = null) }
    }

    fun updateLoginPassword(password: String) {
        _loginState.update { it.copy(password = password, error = null) }
    }

    fun login() {
        val state = _loginState.value
        if (state.email.isBlank()) {
            _loginState.update { it.copy(error = "Please enter your email") }
            return
        }
        if (state.password.isBlank()) {
            _loginState.update { it.copy(error = "Please enter your password") }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(state.email, state.password)) {
                is Resource.Success -> {
                    _loginState.update {
                        it.copy(isLoading = false, isSuccess = true, user = result.data)
                    }
                }
                is Resource.Error -> {
                    _loginState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateRegisterName(name: String) {
        _registerState.update { it.copy(name = name, nameError = null, error = null) }
    }

    fun updateRegisterEmail(email: String) {
        _registerState.update { it.copy(email = email, emailError = null, error = null) }
    }

    fun updateRegisterPhone(phone: String) {
        _registerState.update { it.copy(phone = phone, phoneError = null, error = null) }
    }

    fun updateRegisterPassword(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null, error = null) }
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, error = null) }
    }

    fun register() {
        val state = _registerState.value
        var hasError = false

        if (state.name.isBlank()) {
            _registerState.update { it.copy(nameError = "Name is required") }
            hasError = true
        }
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _registerState.update { it.copy(emailError = "Valid email is required") }
            hasError = true
        }
        if (state.phone.isBlank() || state.phone.length < 10) {
            _registerState.update { it.copy(phoneError = "Valid phone number is required") }
            hasError = true
        }
        if (state.password.length < 6) {
            _registerState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (state.password != state.confirmPassword) {
            _registerState.update { it.copy(confirmPasswordError = "Passwords don't match") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.register(state.name, state.email, state.phone, state.password, state.confirmPassword)) {
                is Resource.Success -> {
                    _registerState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }
                is Resource.Error -> {
                    _registerState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearLoginError() {
        _loginState.update { it.copy(error = null) }
    }

    fun clearRegisterError() {
        _registerState.update { it.copy(error = null) }
    }
}
