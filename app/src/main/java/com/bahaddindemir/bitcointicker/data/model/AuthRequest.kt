package com.bahaddindemir.bitcointicker.data.model

data class AuthRequest(
    var email: String,
    var password: String)
{
    constructor() : this("", "")
}

class LoginValidationException(private val validationType: String) : Exception(validationType)