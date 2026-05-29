package com.bahaddindemir.bitcointicker.ui.detail

sealed interface DetailUiEvent {
    data object DetailLoadFailed : DetailUiEvent
    data object FavoriteChangeFailed : DetailUiEvent
}