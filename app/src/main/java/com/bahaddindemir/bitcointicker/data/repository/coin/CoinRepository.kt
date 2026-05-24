package com.bahaddindemir.bitcointicker.data.repository.coin

import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun loadCoinDetail(coinItemId: String): Flow<CoinResource<CoinDetailItem>>

    fun loadCoins(page: Int): Flow<CoinResource<List<CoinItem>>>

    fun loadFavoriteCoins(): Flow<List<CoinDetailItem>>

    suspend fun addFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem)

    suspend fun deleteFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem)

    fun updateFavoriteCoin(coinDetailItem: CoinDetailItem)

    //ToDo: Implement get favorites from Firestore
    //fun getMyFavoriteCoinList(firebaseUser: FirebaseUser)

    fun getCoinList() : Flow<List<CoinItem>>

    fun getSearchCoinList(searchKey: String) : Flow<List<CoinItem>>

    fun getCoinDetail(coinItemId: String) : Flow<CoinDetailItem?>
}
