package com.bahaddindemir.bitcointicker.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.bahaddindemir.bitcointicker.data.model.LoginValidationException
import com.bahaddindemir.bitcointicker.data.repository.auth.AuthRepository
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.bahaddindemir.bitcointicker.util.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val authRepository: AuthRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    val user by lazy {
        authRepository.currentUser()
    }

    fun onLoginClicked() {
        authenticate { request ->
            authRepository.login(request)
        }
    }

    fun onSignupClicked() {
        authenticate { request ->
            authRepository.register(request)
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { state -> state.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { state -> state.copy(password = value) }
    }

    private fun authenticate(action: suspend (AuthRequest) -> Unit) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            val request = AuthRequest(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            try {
                authUseCase(request)
                _uiState.update { state -> state.copy(isLoading = true) }
                action(request)
                appPreferences.isLoggedIn = true
                _events.emit(AuthUiEvent.AuthSucceeded)
            } catch (exception: LoginValidationException) {
                emitValidationError(exception)
            } catch (exception: Exception) {
                _events.emit(AuthUiEvent.AuthFailed)
                Log.w(this.toString(), exception.message.orEmpty())
            } finally {
                _uiState.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    private suspend fun emitValidationError(exception: LoginValidationException) {
        val validationType = exception.message?.toIntOrNull()
            ?: AuthFieldsValidation.EMPTY_EMAIL.value
        _events.emit(AuthUiEvent.ValidationFailed(validationType))
    }
}
