package com.bahaddindemir.bitcointicker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(private val coinRepository: CoinRepository) : ViewModel() {
    private val coinListPage = MutableSharedFlow<Int>(replay = 1)
    private val searchKeyCoin = MutableStateFlow("")

    val coinState: StateFlow<CoinResource<List<CoinItem>>> = coinListPage
        .flatMapLatest { page -> coinRepository.loadCoins(page) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinResource.Loading
        )

    val searchCoinState: StateFlow<List<CoinItem>> = searchKeyCoin
        .flatMapLatest { searchKey ->
            if (searchKey.isEmpty()) {
                coinRepository.getCoinList()
            } else {
                coinRepository.getSearchCoinList(searchKey)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun postCoinsMarketsPage(page: Int) {
        coinListPage.tryEmit(page)
    }

    fun postSearchCoinsMarketsPage(searchKey: String) {
        searchKeyCoin.value = searchKey
    }
}
