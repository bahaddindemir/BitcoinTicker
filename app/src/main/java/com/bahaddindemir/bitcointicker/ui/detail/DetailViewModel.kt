package com.bahaddindemir.bitcointicker.ui.detail

import androidx.lifecycle.*
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val coinRepository: CoinRepository) : ViewModel() {
    private inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }

    private var coinItem: MutableLiveData<String> = MutableLiveData()
    var coinDetailLiveData: LiveData<CoinResource<CoinDetailItem>> =
        this.coinItem.switchMap { coinItem ->
            launchOnViewModelScope { this.coinRepository.loadCoinDetail(coinItem) }
        }

    val successResponse = SingleLiveEvent<Boolean>()
    private val disposables = CompositeDisposable()

    fun onAddFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val disposable = coinRepository.addFavoriteCoin(firebaseUser, coinDetailItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe({
                                           successResponse.value = true
                                       }, {
                                           successResponse.value = false
                                       })
        disposables.add(disposable)
    }

    fun onDeleteFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val disposable = coinRepository.deleteFavoriteCoin(firebaseUser, coinDetailItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe({
                                           successResponse.value = true
                                       }, {
                                           successResponse.value = false
                                       })
        disposables.add(disposable)
    }

    fun updateFavoriteCoinDetail(coinDetailItem: CoinDetailItem) =
        coinRepository.updateFavoriteCoin(coinDetailItem)

    fun postCoinDetailId(coinItemId: String) = this.coinItem.postValue(coinItemId)
}