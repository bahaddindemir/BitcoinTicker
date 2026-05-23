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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val authRepository: AuthRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    var request = AuthRequest()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _validationException = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val validationException = _validationException.asSharedFlow()

    private val _successResponse = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val successResponse = _successResponse.asSharedFlow()

    val user by lazy {
        authRepository.currentUser()
    }

    fun onLoginClicked() {
        authenticate(::login)
    }

    fun onSignupClicked() {
        authenticate(::signup)
    }

    private fun authenticate(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                authUseCase(request)
                _isLoading.value = true
                action()
                appPreferences.isLoggedIn = true
                _successResponse.emit(true)
            } catch (exception: LoginValidationException) {
                emitValidationError(exception)
            } catch (exception: Exception) {
                _successResponse.emit(false)
                Log.w(this.toString(), exception.message.orEmpty())
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun login() {
        authRepository.login(request)
    }

    private suspend fun signup() {
        authRepository.register(request)
    }

    private suspend fun emitValidationError(exception: LoginValidationException) {
        val validationType = exception.message?.toIntOrNull()
            ?: AuthFieldsValidation.EMPTY_EMAIL.value
        _validationException.emit(validationType)
    }
}
