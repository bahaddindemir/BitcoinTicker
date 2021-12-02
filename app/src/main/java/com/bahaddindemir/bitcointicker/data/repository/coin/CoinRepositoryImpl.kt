package com.bahaddindemir.bitcointicker.data.repository.coin

import android.util.Log
import androidx.lifecycle.LiveData
import com.bahaddindemir.bitcointicker.data.local.CoinDao
import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import com.bahaddindemir.bitcointicker.data.model.Envelope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.repository.network.NetworkBoundRepository
import com.bahaddindemir.bitcointicker.data.services.ApiService
import com.bahaddindemir.bitcointicker.data.services.FireStoreSource
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class CoinRepositoryImp @Inject constructor(private val coinDao: CoinDao,
                                            private val fireStore: FireStoreSource,
                                            private val apiService: ApiService,
                                            private val appPreferences: AppPreferences) : CoinRepository
{
    override var isLoading: Boolean = false

    override suspend fun loadCoinDetail(coinItemId: String):
            LiveData<CoinResource<CoinDetailItem>> = withContext(Dispatchers.IO) {
            return@withContext object : NetworkBoundRepository<CoinDetailItem, CoinDetailItem>() {
                override fun saveFetchData(items: CoinDetailItem) {
                    items.let {
                        coinDao.updateCoinDetail(it)
                    }
                }

                override fun shouldFetch(data: CoinDetailItem?): Boolean {
                    return true
                }

                override fun loadFromDb(): LiveData<CoinDetailItem> {
                    return getCoinDetail(coinItemId)
                }

                override fun fetchService(): LiveData<ApiResponse<CoinDetailItem>> {
                    return apiService.fetchCoinsDetail(coinItemId)
                }

                override fun onFetchFailed(envelope: Envelope?) {
                    Log.w(this.toString(),"onFetchFailed : $envelope")
                }
            }.asLiveData()
        }

    override suspend fun loadCoins(page: Int): LiveData<CoinResource<List<CoinItem>>> =
        withContext(Dispatchers.IO) {
            return@withContext object : NetworkBoundRepository<List<CoinItem>, List<CoinItem>>() {
                override fun saveFetchData(items: List<CoinItem>) {
                    items.let {
                        coinDao.insertCoins(it)
                    }
                }

                override fun shouldFetch(data: List<CoinItem>?): Boolean {
                    return data == null || data.isEmpty()
                }

                override fun loadFromDb(): LiveData<List<CoinItem>> {
                    return getCoinList()
                }

                override fun fetchService(): LiveData<ApiResponse<List<CoinItem>>> {
                    val map = HashMap<String, Any>()
                    val defaultCurrency = appPreferences.defaultCurrency
                    defaultCurrency?.let { map[vsCurrency] = it.lowercase(Locale.ROOT) }
                    map[order] = "market_cap_desc"
                    map[pageMap] = page
                    map[perPage] = "20"
                    map[sparkline] = false
                    map[priceChangePercentage] = "24h"
                    return apiService.fetchCoins(map)
                }

                override fun onFetchFailed(envelope: Envelope?) {
                    Log.w(this.toString(),"onFetchFailed : $envelope")
                }
            }.asLiveData()
        }

    override fun dataAddCoinFavorite(firebaseUser: FirebaseUser, coinDetailItem: CoinDetailItem) =
        fireStore.addCoinToFavorite(firebaseUser, coinDetailItem)

    //fun getMyFavoriteCoinList(firebaseUser: FirebaseUser) = fireStore.getMyCoinFavoriteList(firebaseUser)

    override fun getCoinList() = coinDao.getCoins()

    override fun getSearchCoinList(searchKey: String) = coinDao.searchCoins(searchKey)

    override fun getCoinDetail(coinItemId: String) = coinDao.getCoinDetail(coinItemId)

    override fun getCoinFavoriteUpdate(coinDetailItem: CoinDetailItem) {
        coinDao.updateCoinDetail(coinDetailItem)
    }

    companion object {
        private const val order = "market_cap_desc"
        private const val pageMap = "page"
        const val perPage = "per_page"
        const val sparkline = "sparkline"
        const val vsCurrency = "vs_currency"
        const val priceChangePercentage = "price_change_percentage"
    }
}