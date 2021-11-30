package com.bahaddindemir.bitcointicker.ui.detail

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.coin.CoinRepository
import com.bahaddindemir.bitcointicker.ui.base.BaseViewModel
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val coinRepository: CoinRepository) : BaseViewModel() {
    private inline fun <T> launchOnViewModelScope(crossinline block: suspend () -> LiveData<T>): LiveData<T> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(block())
        }
    }

    //Todo: Integrate to fragment res file
    private var coinItem: MutableLiveData<String> = MutableLiveData()
    private var coinItemId: MutableLiveData<String> = MutableLiveData()
    var coinDetailLiveData: LiveData<CoinResource<CoinDetailItem>>

    val successResponse = SingleLiveEvent<Void>()
    val failResponse = SingleLiveEvent<Void>()
    private val disposables = CompositeDisposable()
    val favourite = ObservableBoolean()
    private lateinit var coinDetailItem: CoinDetailItem

    init {
        Log.v(this.toString(),"Injection MainActivityViewModel")

        this.coinDetailLiveData = this.coinItem.switchMap { coinItem ->
            launchOnViewModelScope {
                this.coinRepository.loadCoinDetail(coinItem)
            }
        }
    }

    fun onAddFavoriteFireStore(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) {
        val disposable = coinRepository.dataAddCoinFavorite(firebaseUser, coinDetailItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                successResponse.call()
            }, {
                it.message?.run {
                    failResponse.call()
                }
            })
        disposables.add(disposable)
    }

    fun postCoinDetailId(currencyItemId: String) = this.coinItem.postValue(currencyItemId)
}