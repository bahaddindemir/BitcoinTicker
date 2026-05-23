package com.bahaddindemir.bitcointicker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(private val coinRepository: CoinRepository) : ViewModel() {
    private val coinItem = MutableStateFlow<String?>(null)

    val coinDetailState: StateFlow<CoinResource<CoinDetailItem>> = coinItem
        .filterNotNull()
        .flatMapLatest { coinItemId -> coinRepository.loadCoinDetail(coinItemId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CoinResource.loading(null, null)
        )

    private val _successResponse = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val successResponse = _successResponse.asSharedFlow()
    private val disposables = CompositeDisposable()

    fun onAddFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val disposable = coinRepository.addFavoriteCoin(firebaseUser, coinDetailItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe({
                                           _successResponse.tryEmit(true)
                                       }, {
                                           _successResponse.tryEmit(false)
                                       })
        disposables.add(disposable)
    }

    fun onDeleteFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val disposable = coinRepository.deleteFavoriteCoin(firebaseUser, coinDetailItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe({
                                           _successResponse.tryEmit(true)
                                       }, {
                                           _successResponse.tryEmit(false)
                                       })
        disposables.add(disposable)
    }

    fun updateFavoriteCoinDetail(coinDetailItem: CoinDetailItem) =
        coinRepository.updateFavoriteCoin(coinDetailItem)

    fun setCoinDetailId(coinItemId: String) {
        coinItem.value = coinItemId
    }
}
