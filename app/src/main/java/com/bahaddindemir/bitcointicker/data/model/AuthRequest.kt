package com.bahaddindemir.bitcointicker.data.model

data class AuthRequest(
    var email: String,
    var password: String)
{
    constructor() : this("", "")
}

class LoginValidationException(validationType: String) : Exception(validationType)