package com.bahaddindemir.bitcointicker.data.repository.auth

import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.core.Completable

interface AuthRepository {
    fun login(request: AuthRequest) : Completable

    fun register(request: AuthRequest) : Completable

    fun logout()

    fun currentUser() : FirebaseUser?
}