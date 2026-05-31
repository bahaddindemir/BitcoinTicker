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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(private val coinRepository: CoinRepository) : ViewModel() {
    private val coinListPage = MutableSharedFlow<Int>(replay = 1)
    private val searchKeyCoin = MutableStateFlow("")

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private val coinState: StateFlow<CoinResource<List<CoinItem>>> = coinListPage
        .flatMapLatest { page -> coinRepository.loadCoins(page) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinResource.Loading
        )

    private val searchCoinState: StateFlow<List<CoinItem>> = searchKeyCoin
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

    init {
        loadCoins()
        observeCoins()
        observeSearchCoins()
    }

    fun loadCoins(page: Int = 1) {
        coinListPage.tryEmit(page)
    }

    fun onSearchClick() {
        _uiState.update { state -> state.copy(isSearchVisible = true) }
    }

    fun onSearchChange(value: String) {
        val searchText = value.trim()
        _uiState.update { state -> state.copy(searchText = searchText) }
        searchKeyCoin.value = searchText
    }

    fun onCloseSearchClick() {
        _uiState.update { state ->
            state.copy(
                isSearchVisible = false,
                searchText = ""
            )
        }
        searchKeyCoin.value = ""
    }

    private fun observeCoins() {
        viewModelScope.launch {
            coinState.collect { resource ->
                when (resource) {
                    CoinResource.Loading -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = true,
                                isContentVisible = false
                            )
                        }
                    }

                    is CoinResource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                coins = resource.data.orEmpty(),
                                isLoading = false,
                                isContentVisible = true
                            )
                        }
                    }

                    is CoinResource.Error -> {
                        _uiState.update { state -> state.copy(isLoading = false) }
                        _events.emit(HomeUiEvent.CoinsLoadFailed)
                    }
                }
            }
        }
    }

    private fun observeSearchCoins() {
        viewModelScope.launch {
            searchCoinState.collect { coins ->
                _uiState.update { state -> state.copy(coins = coins) }
            }
        }
    }
}
