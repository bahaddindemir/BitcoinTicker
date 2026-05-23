package com.bahaddindemir.bitcointicker.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.bahaddindemir.bitcointicker.util.AuthUseCase
import com.bahaddindemir.bitcointicker.data.model.Resource
import com.bahaddindemir.bitcointicker.data.repository.auth.AuthRepository
import com.bahaddindemir.bitcointicker.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase,
                                        private val authRepository: AuthRepository,
                                        private val appPreferences: AppPreferences) : ViewModel()
{
    var request = AuthRequest()
    private val _authResponse = MutableStateFlow<Any>(Resource.Default)
    val authResponse = _authResponse

    private val _validationException = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val validationException = _validationException.asSharedFlow()

    private val _successResponse = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val successResponse = _successResponse.asSharedFlow()

    val user by lazy {
        authRepository.currentUser()
    }

    fun onLoginClicked() {
        authUseCase()
        login()
    }

    fun onSignupClicked() {
        authUseCase()
        signup()
    }

    private fun login() {
        viewModelScope.launch {
            try {
                authRepository.login(request)
                appPreferences.isLoggedIn = true
                _successResponse.emit(true)
            } catch (exception: Exception) {
                _successResponse.emit(false)
                Log.w(this.toString(), exception.message.orEmpty())
            }
        }
    }

    private fun signup() {
        viewModelScope.launch {
            try {
                authRepository.register(request)
                appPreferences.isLoggedIn = true
                _successResponse.emit(true)
            } catch (exception: Exception) {
                _successResponse.emit(false)
                Log.w(this.toString(), exception.message.orEmpty())
            }
        }
    }

    private fun authUseCase() {
        authUseCase(request)
            .catch { exception ->
                exception.message?.toIntOrNull()?.let { _validationException.emit(it) }
            }
            .onEach { result -> _authResponse.value = result }
            .launchIn(viewModelScope)
    }
}
