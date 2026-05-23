package com.bahaddindemir.bitcointicker.data.repository.auth

import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.bahaddindemir.bitcointicker.data.services.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val remoteDataSource: AuthRemoteDataSource) :
    AuthRepository {

    override suspend fun login(request: AuthRequest) = remoteDataSource.login(request)

    override suspend fun register(request: AuthRequest) = remoteDataSource.register(request)

    override
    fun logout() = remoteDataSource.logout()

    override
    fun currentUser() = remoteDataSource.currentUser()
}
