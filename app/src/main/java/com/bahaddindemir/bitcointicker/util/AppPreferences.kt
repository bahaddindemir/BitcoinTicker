package com.bahaddindemir.bitcointicker.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AppPreferences @Inject constructor(private val context: Context) {
    companion object {
        private const val APP_PREFERENCES_NAME = "APP-NAME-Cache"
        private const val SESSION_PREFERENCES_NAME = "APP-NAME-UserCache"
        private const val MODE = Context.MODE_PRIVATE

        private val FIRST_TIME = Pair("FIRST_TIME", false)
        private val LOGGED_IN = Pair("LOGGED_IN", false)
        private val DEFAULT_LANGUAGE = Pair("DEFAULT_LANGUAGE", "English")
        private val DEFAULT_CURRENCY = Pair("DEFAULT_CURRENCY", "BTC")
    }

    private val appPreferences: SharedPreferences =
        context.getSharedPreferences(APP_PREFERENCES_NAME, MODE)
    private val sessionPreferences: SharedPreferences =
        context.getSharedPreferences(SESSION_PREFERENCES_NAME, MODE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isLoggedIn: Boolean
        get() {
            return sessionPreferences.getBoolean(LOGGED_IN.first, LOGGED_IN.second)
        }
        set(value) = sessionPreferences.edit {
            it.putBoolean(LOGGED_IN.first, value)
        }

    var isFirstTime: Boolean
        get() {
            return appPreferences.getBoolean(FIRST_TIME.first, FIRST_TIME.second)
        }
        set(value) = appPreferences.edit {
            it.putBoolean(FIRST_TIME.first, value)
        }

    var defaultLanguage: String?
        get() {
            return appPreferences.getString(DEFAULT_LANGUAGE.first, DEFAULT_LANGUAGE.second)
        }
        set(value) = appPreferences.edit {
            it.putString(DEFAULT_LANGUAGE.first, value)
        }

    var defaultCurrency: String?
        get() {
            return appPreferences.getString(DEFAULT_CURRENCY.first, DEFAULT_CURRENCY.second)
        }
        set(value) = appPreferences.edit {
            it.putString(DEFAULT_CURRENCY.first, value)
        }

    fun clearPreferences() {
        sessionPreferences.edit {
            it.clear().apply()
        }
    }
}