package com.sweetshop.admin.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.domain.repository.AuthRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        if (authRepository.isLoggedIn()) {
            _state.update { it.copy(isLoggedIn = true) }
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun login() {
        val currentState = _state.value
        if (currentState.email.isBlank()) {
            _state.update { it.copy(error = "Email is required") }
            return
        }
        if (currentState.password.isBlank()) {
            _state.update { it.copy(error = "Password is required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(currentState.email, currentState.password)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update { LoginState() }
        }
    }
}
