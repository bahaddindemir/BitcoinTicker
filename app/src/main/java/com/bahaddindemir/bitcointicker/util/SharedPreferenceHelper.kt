package com.bahaddindemir.bitcointicker.util

import com.orhanobut.hawk.Hawk

object SharedPreferenceHelper {
    fun <T> saveSharedData(key: String?, value: T) {
        Hawk.put(key, value)
    }

    fun <T> getSharedData(key: String?): T? {
        return if (Hawk.get<Any?>(key) != null) {
            Hawk.get(key)
        } else null
    }

    fun allDeletePref() {
        Hawk.deleteAll()
    }
}