package com.bahaddindemir.bitcointicker.extension

import com.bahaddindemir.bitcointicker.util.SharedPreferenceHelper

fun Float.marketCapToText(): String {
    val currency = (SharedPreferenceHelper.getSharedData("DEFAULT_CURRENCY") as? String) ?: "BTC"
    val value = this.toString().take(6)

    return "$value $currency"
}

fun Float.priceChangeToText(): String {
    val value = this.toString().take(5)

    return "$value %"
}

fun Float.isNegative(): Boolean = this < 0