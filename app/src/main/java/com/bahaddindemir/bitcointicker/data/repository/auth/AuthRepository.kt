package com.bahaddindemir.bitcointicker.data.repository.auth

import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(request: AuthRequest)

    suspend fun register(request: AuthRequest)

    fun logout()

    fun currentUser() : FirebaseUser?
}
