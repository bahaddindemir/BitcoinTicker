package com.bahaddindemir.bitcointicker.data.repository.coin

import androidx.lifecycle.LiveData
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.core.Completable

interface CoinRepository {
    var isLoading: Boolean

    suspend fun loadCoinDetail(coinItemId: String): LiveData<CoinResource<CoinDetailItem>>

    suspend fun loadCoins(page: Int): LiveData<CoinResource<List<CoinItem>>>

    fun loadFavoriteCoins(): LiveData<List<CoinDetailItem>>

    fun addFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) : Completable

    fun deleteFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) : Completable

    fun updateFavoriteCoin(coinDetailItem: CoinDetailItem)

    //ToDo: Implement get favorites
    //fun getMyFavoriteCoinList(firebaseUser: FirebaseUser)

    fun getCoinList() : LiveData<List<CoinItem>>

    fun getSearchCoinList(searchKey: String) : LiveData<List<CoinItem>>

    fun getCoinDetail(coinItemId: String) : LiveData<CoinDetailItem>
}