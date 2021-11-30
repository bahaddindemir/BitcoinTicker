package com.bahaddindemir.bitcointicker.data.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSource @Inject constructor() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun login(request: AuthRequest) = Completable.create {
        firebaseAuth.signInWithEmailAndPassword(request.email, request.password)
            .addOnCompleteListener { task ->
                if (!it.isDisposed) {
                    if (task.isSuccessful) {
                        Log.d(this.toString(), "signInWithEmail:success")
                        it.onComplete()
                    } else {
                        Log.w(this.toString(), "signInWithEmail:failure", task.exception)
                        it.onError(task.exception!!)
                    }
                }
            }
    }

    fun register(request: AuthRequest) = Completable.create {
        firebaseAuth.createUserWithEmailAndPassword(request.email, request.password)
            .addOnCompleteListener { task ->
                if (!it.isDisposed) {
                    if (task.isSuccessful) {
                        Log.d(this.toString(), "createUserWithEmail:success")
                        it.onComplete()
                    } else {
                        Log.w(this.toString(), "createUserWithEmail:failure", task.exception)
                        it.onError(task.exception!!)
                    }
                }
            }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser
}