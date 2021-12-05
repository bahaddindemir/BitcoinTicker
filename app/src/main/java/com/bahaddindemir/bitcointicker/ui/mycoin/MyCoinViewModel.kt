package com.bahaddindemir.bitcointicker.ui.mycoin

import androidx.lifecycle.*
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MyCoinViewModel @Inject constructor(private val coinRepository: CoinRepository) : BaseViewModel() {
    private inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }

    private var coinDetailItem: MutableLiveData<String> = MutableLiveData()
    var coinLiveData: LiveData<List<CoinDetailItem>> =
        this.coinDetailItem.switchMap {
            launchOnViewModelScope { this.coinRepository.loadFavoriteCoins() }
        }
}