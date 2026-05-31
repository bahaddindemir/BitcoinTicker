package com.bahaddindemir.bitcointicker.data.repository.coin

import android.util.Log
import com.bahaddindemir.bitcointicker.data.local.CoinDao
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.network.NetworkBoundRepository
import com.bahaddindemir.bitcointicker.data.services.ApiService
import com.bahaddindemir.bitcointicker.data.services.FireStoreSource
import com.bahaddindemir.bitcointicker.util.PreferencesStore
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
                                            private val appPreferences: PreferencesStore) : CoinRepository
{
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

                override fun onFetchFailed(message: String) {
                    Log.w(this.toString(),"onFetchFailed : $message")
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
                    defaultCurrency?.let { map[VS_CURRENCY] = it.lowercase(Locale.ROOT) }
                    map[ORDER] = ORDER_MARKET_CAP_DESC
                    map[PAGE] = page
                    map[PER_PAGE] = PAGE_SIZE.toString()
                    map[SPARKLINE] = false
                    map[PRICE_CHANGE_PERCENTAGE] = PRICE_CHANGE_24H
                    return apiService.fetchCoins(map)
                }

                override fun onFetchFailed(message: String) {
                    Log.w(this.toString(),"onFetchFailed : $message")
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
       private const val ORDER = "order"
        private const val ORDER_MARKET_CAP_DESC = "market_cap_desc"
        private const val PAGE = "page"
        private const val PAGE_SIZE = 20
        private const val PER_PAGE = "per_page"
        private const val SPARKLINE = "sparkline"
        private const val VS_CURRENCY = "vs_currency"
        private const val PRICE_CHANGE_PERCENTAGE = "price_change_percentage"
        private const val PRICE_CHANGE_24H = "24h"
    }
}
