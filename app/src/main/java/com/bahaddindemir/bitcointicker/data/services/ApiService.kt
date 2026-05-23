package com.bahaddindemir.bitcointicker.data.services

import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    @GET("coins/markets")
    @Headers("Accept: application/json")
    suspend fun fetchCoins(@QueryMap map: HashMap<String, Any>): Response<List<CoinItem>>

    @GET("coins/{id}")
    @Headers("Accept: application/json")
    suspend fun fetchCoinsDetail(@Path("id") id: String): Response<CoinDetailItem>
}
