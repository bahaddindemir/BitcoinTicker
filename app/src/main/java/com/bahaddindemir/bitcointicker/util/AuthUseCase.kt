package com.bahaddindemir.bitcointicker.util

import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.bahaddindemir.bitcointicker.data.model.LoginValidationException
import com.bahaddindemir.bitcointicker.extension.isValidEmail
import javax.inject.Inject

class AuthUseCase @Inject constructor() {
    @Throws(LoginValidationException::class)
    operator fun invoke(request: AuthRequest) {
        if (request.email.isEmpty()) {
            throw LoginValidationException(AuthFieldsValidation.EMPTY_EMAIL.value.toString())
        }

        if (!request.email.isValidEmail()) {
            throw LoginValidationException(AuthFieldsValidation.INVALID_EMAIL.value.toString())
        }

        if (request.password.isEmpty()) {
            throw LoginValidationException(AuthFieldsValidation.EMPTY_PASSWORD.value.toString())
        }
    }
}
