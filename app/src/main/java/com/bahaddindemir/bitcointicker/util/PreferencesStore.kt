package com.bahaddindemir.bitcointicker.util

interface PreferencesStore {
    var isLoggedIn: Boolean
    var isFirstTime: Boolean
    var defaultLanguage: String?
    var defaultCurrency: String?

    fun clearPreferences()
}
