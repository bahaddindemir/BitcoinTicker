package com.bahaddindemir.bitcointicker.data.model.coin

sealed interface CoinResource<out T> {
    data object Loading : CoinResource<Nothing>

    data class Success<out T>(
        val data: T?,
        val nextPage: Int?
    ) : CoinResource<T>

    data class Error<out T>(
        val message: String,
        val data: T?
    ) : CoinResource<T>
}
