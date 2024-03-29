package com.bahaddindemir.bitcointicker.ui.mycoin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MyCoinViewModel @Inject constructor(private val coinRepository: CoinRepository) : ViewModel() {
    private inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }

    var coinLiveData: LiveData<List<CoinDetailItem>> = launchOnViewModelScope {
        this.coinRepository.loadFavoriteCoins()
    }
}