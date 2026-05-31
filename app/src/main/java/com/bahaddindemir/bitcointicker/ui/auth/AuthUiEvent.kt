package com.bahaddindemir.bitcointicker.ui.auth

sealed interface AuthUiEvent {
    data object AuthSucceeded : AuthUiEvent
    data object AuthFailed : AuthUiEvent
    data class ValidationFailed(val validationType: Int) : AuthUiEvent
}
