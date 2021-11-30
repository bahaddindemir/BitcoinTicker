package com.bahaddindemir.bitcointicker.util

import com.bahaddindemir.bitcointicker.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthUseCase @Inject constructor()
{
    @Throws(LoginValidationException::class)
    operator fun invoke(request: AuthRequest): Flow<Any> = flow {
        if (request.email.isEmpty()) {
            throw LoginValidationException(AuthFieldsValidation.EMPTY_EMAIL.value.toString())
        }

        if (!request.email.isValidEmail()) {
            throw LoginValidationException(AuthFieldsValidation.INVALID_EMAIL.value.toString())
        }

        if (request.password.isEmpty()) {
            throw LoginValidationException(AuthFieldsValidation.EMPTY_PASSWORD.value.toString())
        }

        emit(Resource.Loading)
    }.flowOn(Dispatchers.IO)
}