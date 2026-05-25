package com.bahaddindemir.bitcointicker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val coinItem = MutableSharedFlow<String>(replay = 1)

    val defaultCurrency: String
        get() = appPreferences.defaultCurrency ?: "BTC"

    val coinDetailState: StateFlow<CoinResource<CoinDetailItem>> = coinItem
        .flatMapLatest { coinItemId -> coinRepository.loadCoinDetail(coinItemId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinResource.Loading
        )

    private val _successResponse = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val successResponse = _successResponse.asSharedFlow()

    fun onAddFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        viewModelScope.launch {
            try {
                coinRepository.addFavoriteCoin(firebaseUser, coinDetailItem)
                _successResponse.emit(true)
            } catch (_: Exception) {
                _successResponse.emit(false)
            }
        }
    }

    fun onDeleteFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        viewModelScope.launch {
            try {
                coinRepository.deleteFavoriteCoin(firebaseUser, coinDetailItem)
                _successResponse.emit(true)
            } catch (_: Exception) {
                _successResponse.emit(false)
            }
        }
    }

    fun updateFavoriteCoinDetail(coinDetailItem: CoinDetailItem) =
        coinRepository.updateFavoriteCoin(coinDetailItem)

    fun setCoinDetailId(coinItemId: String) {
        coinItem.tryEmit(coinItemId)
    }
}
