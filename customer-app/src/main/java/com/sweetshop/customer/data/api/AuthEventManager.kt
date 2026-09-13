package com.sweetshop.customer.data.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventManager @Inject constructor() {
    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    fun emitSessionExpired() {
        _authEvents.tryEmit(AuthEvent.SessionExpired)
    }
}

sealed class AuthEvent {
    data object SessionExpired : AuthEvent()
}
