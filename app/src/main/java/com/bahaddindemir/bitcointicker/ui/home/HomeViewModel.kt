package com.bahaddindemir.bitcointicker.ui.home

import androidx.lifecycle.*
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.model.FetchStatus
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val coinRepository: CoinRepository) : BaseViewModel() {
    private inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }

    private var coinListPage: MutableLiveData<Int> = MutableLiveData()
    private var searchKeyCoin: MutableLiveData<String> = MutableLiveData()
    private var fetchStatus = FetchStatus()

    val coinLiveData: LiveData<CoinResource<List<CoinItem>>> = this.coinListPage.switchMap { page ->
        launchOnViewModelScope { this.coinRepository.loadCoins(page) }
    }
    var searchCoinLiveData: LiveData<List<CoinItem>> = this.searchKeyCoin.switchMap { searchKey ->
        launchOnViewModelScope { this.coinRepository.getSearchCoinList(searchKey) }
    }

    init {
        this.searchCoinLiveData = launchOnViewModelScope {
            this.coinRepository.getCoinList()
        }
    }

    fun fetchStatus(coinResource: CoinResource<List<CoinItem>>) {
        fetchStatus = coinResource.fetchStatus
    }

    fun postCoinsMarketsPage(page: Int) = this.coinListPage.postValue(page)

    fun postSearchCoinsMarketsPage(searchKey: String) =
        this.coinRepository.getSearchCoinList(searchKey)

    fun postSearchFullCoinsMarketsPage() = this.coinRepository.getCoinList()
}