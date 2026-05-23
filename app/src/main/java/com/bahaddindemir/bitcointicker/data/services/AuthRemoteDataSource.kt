package com.bahaddindemir.bitcointicker.data.services

import com.google.firebase.auth.FirebaseAuth
import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSource @Inject constructor() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(request: AuthRequest) {
        firebaseAuth.signInWithEmailAndPassword(request.email, request.password).await()
    }

    suspend fun register(request: AuthRequest) {
        firebaseAuth.createUserWithEmailAndPassword(request.email, request.password).await()
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser
}