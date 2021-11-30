package com.bahaddindemir.bitcointicker.data.repository.account

interface AccountRepository {
    fun logOut()

    fun isFirstTime(): Boolean

    fun isLoggedIn(): Boolean

    fun setFirstTime(isFirstTime: Boolean)

    fun clearPreferences()
}