package com.bahaddindemir.bitcointicker.data.services

import androidx.lifecycle.LiveData
import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    @GET("coins/markets")
    @Headers("Accept: application/json")
    fun fetchCoins(@QueryMap map: HashMap<String, Any>): LiveData<ApiResponse<List<CoinItem>>>

    @GET("coins/{coinId}")
    @Headers("Accept: application/json")
    fun fetchCoinsDetail(@Path("coinId") coinId: String): LiveData<ApiResponse<CoinDetailItem>>
}