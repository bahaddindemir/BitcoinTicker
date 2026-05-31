package com.bahaddindemir.bitcointicker.ui.home

sealed interface HomeUiEvent {
    data object CoinsLoadFailed : HomeUiEvent
}
