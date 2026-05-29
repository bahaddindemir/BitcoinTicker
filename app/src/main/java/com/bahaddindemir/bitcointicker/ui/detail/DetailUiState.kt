package com.bahaddindemir.bitcointicker.ui.detail

import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem

data class DetailUiState(
    val coinId: String? = null,
    val title: String = "",
    val coinDetailItem: CoinDetailItem? = null,
    val defaultCurrency: String = "BTC",
    val intervalText: String = DEFAULT_REFRESH_INTERVAL_MILLIS.toString(),
    val refreshIntervalMillis: Long = DEFAULT_REFRESH_INTERVAL_MILLIS,
    val isFavorite: Boolean = false,
    val lastUpdatedDate: String = "",
    val isLoading: Boolean = false,
)

const val DEFAULT_REFRESH_INTERVAL_MILLIS = 2000L
