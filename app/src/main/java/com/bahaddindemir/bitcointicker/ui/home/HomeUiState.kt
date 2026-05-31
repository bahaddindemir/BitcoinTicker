package com.bahaddindemir.bitcointicker.ui.home

import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem

data class HomeUiState(
    val coins: List<CoinItem> = emptyList(),
    val isSearchVisible: Boolean = false,
    val searchText: String = "",
    val isContentVisible: Boolean = false,
    val isLoading: Boolean = false,
)
