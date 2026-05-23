package com.bahaddindemir.bitcointicker.data.repository.coin

import android.util.Log
import com.bahaddindemir.bitcointicker.data.local.CoinDao
import com.bahaddindemir.bitcointicker.data.model.Envelope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.network.NetworkBoundRepository
import com.bahaddindemir.bitcointicker.data.services.ApiService
import com.bahaddindemir.bitcointicker.data.services.FireStoreSource
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepositoryImp @Inject constructor(private val coinDao: CoinDao,
                                            private val fireStore: FireStoreSource,
                                            private val apiService: ApiService,
                                            private val appPreferences: AppPreferences) : CoinRepository
{
    override var isLoading: Boolean = false

    override fun loadCoinDetail(coinItemId: String): Flow<CoinResource<CoinDetailItem>> =
            object : NetworkBoundRepository<CoinDetailItem, CoinDetailItem>() {
                override fun saveFetchData(items: CoinDetailItem) {
                    items.let {
                        coinDao.updateCoinDetail(it)
                    }
                }

                override fun shouldFetch(data: CoinDetailItem?): Boolean {
                    return data == null
                }

                override fun loadFromDb(): Flow<CoinDetailItem?> {
                    return getCoinDetail(coinItemId)
                }

                override suspend fun fetchService(): Response<CoinDetailItem> {
                    return apiService.fetchCoinsDetail(coinItemId)
                }

                override fun onFetchFailed(envelope: Envelope?) {
                    Log.w(this.toString(),"onFetchFailed : $envelope")
                }
            }.asFlow()

    override fun loadCoins(page: Int): Flow<CoinResource<List<CoinItem>>> =
            object : NetworkBoundRepository<List<CoinItem>, List<CoinItem>>() {
                override fun saveFetchData(items: List<CoinItem>) {
                    items.let {
                        coinDao.insertCoins(it)
                    }
                }

                override fun shouldFetch(data: List<CoinItem>?): Boolean {
                    return data.isNullOrEmpty()
                }

                override fun loadFromDb(): Flow<List<CoinItem>?> {
                    return getCoinList()
                }

                override suspend fun fetchService(): Response<List<CoinItem>> {
                    val map = HashMap<String, Any>()
                    val defaultCurrency = appPreferences.defaultCurrency
                    defaultCurrency?.let { map[vsCurrency] = it.lowercase(Locale.ROOT) }
                    map[order] = order
                    map[pageMap] = page
                    map[perPage] = "20"
                    map[sparkline] = false
                    map[priceChangePercentage] = "24h"
                    return apiService.fetchCoins(map)
                }

                override fun onFetchFailed(envelope: Envelope?) {
                    Log.w(this.toString(),"onFetchFailed : $envelope")
                }
            }.asFlow()

    override fun loadFavoriteCoins(): Flow<List<CoinDetailItem>> = coinDao.getFavoriteCoins()

    override suspend fun addFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) =
        fireStore.addCoinToFavorite(firebaseUser, coinDetailItem)

    override suspend fun deleteFavoriteCoin(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) =
        fireStore.deleteFavoriteCoin(firebaseUser, coinDetailItem)

    override fun updateFavoriteCoin(coinDetailItem: CoinDetailItem) {
        coinDao.updateCoinDetail(coinDetailItem)
    }

    //ToDo: Implement get favorites from Firestore
    //fun getMyFavoriteCoinList(firebaseUser: FirebaseUser) = fireStore.getMyCoinFavoriteList(firebaseUser)

    override fun getCoinList() = coinDao.getCoins()

    override fun getSearchCoinList(searchKey: String) = coinDao.searchCoins(searchKey)

    override fun getCoinDetail(coinItemId: String) = coinDao.getCoinDetail(coinItemId)

    companion object {
        private const val order = "market_cap_desc"
        private const val pageMap = "page"
        const val perPage = "per_page"
        const val sparkline = "sparkline"
        const val vsCurrency = "vs_currency"
        const val priceChangePercentage = "price_change_percentage"
    }
}
