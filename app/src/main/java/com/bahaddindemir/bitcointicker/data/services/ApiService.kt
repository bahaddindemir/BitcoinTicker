package com.bahaddindemir.bitcointicker.data.services

import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    @GET("coins/markets")
    @Headers("Accept: application/json")
    fun fetchCoins(@QueryMap map: HashMap<String, Any>): Flow<ApiResponse<List<CoinItem>>>

    @GET("coins/{id}")
    @Headers("Accept: application/json")
    fun fetchCoinsDetail(@Path("id") id: String): Flow<ApiResponse<CoinDetailItem>>
}