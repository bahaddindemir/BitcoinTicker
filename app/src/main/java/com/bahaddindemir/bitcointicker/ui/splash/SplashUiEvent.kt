package com.bahaddindemir.bitcointicker.ui.splash

sealed interface SplashUiEvent {
    data object OpenLogin : SplashUiEvent
    data object OpenHome : SplashUiEvent
}