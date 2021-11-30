package com.bahaddindemir.bitcointicker.data.repository.account

import com.bahaddindemir.bitcointicker.data.services.AuthRemoteDataSource
import com.bahaddindemir.bitcointicker.util.AppPreferences
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val appPreferences: AppPreferences) : AccountRepository {

    override
    fun logOut() = authRemoteDataSource.logout()

    override
    fun isFirstTime() = appPreferences.isFirstTime

    override
    fun isLoggedIn() = appPreferences.isLoggedIn

    override
    fun setFirstTime(isFirstTime: Boolean) {
        appPreferences.isFirstTime = isFirstTime
    }

    override
    fun clearPreferences() = appPreferences.clearPreferences()
}