package com.bahaddindemir.bitcointicker.ui.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)
