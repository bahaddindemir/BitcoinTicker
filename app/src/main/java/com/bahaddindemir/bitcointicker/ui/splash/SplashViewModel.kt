package com.bahaddindemir.bitcointicker.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.util.GeneralUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val generalUseCases: GeneralUseCases) : ViewModel() {
    private var isStarted = false

    private val _events = MutableSharedFlow<SplashUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    companion object {
        private const val SPLASH_DELAY_MILLIS = 2000L
    }

    fun start() {
        if (isStarted) return
        isStarted = true

        viewModelScope.launch {
            delay(SPLASH_DELAY_MILLIS)
            when {
                generalUseCases.checkFirstTimeUseCase() -> {
                    generalUseCases.setFirstTimeUseCase(false)
                    _events.emit(SplashUiEvent.OpenLogin)
                }

                generalUseCases.checkLoggedInUserUseCase() -> {
                    _events.emit(SplashUiEvent.OpenHome)
                }

                else -> {
                    _events.emit(SplashUiEvent.OpenLogin)
                }
            }
        }
    }
}