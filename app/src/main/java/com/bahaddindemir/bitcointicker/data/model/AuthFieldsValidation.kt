package com.bahaddindemir.bitcointicker.data.model

enum class AuthFieldsValidation(val value: Int) {
    EMPTY_EMAIL(1),
    INVALID_EMAIL(2),
    EMPTY_PASSWORD(3)
}