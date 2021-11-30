package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinPrice
import com.google.gson.Gson

class CoinPriceConverter {
    @TypeConverter
    fun stringToMarket(value: String): CoinPrice? {
        return Gson().fromJson(value, CoinPrice::class.java)
    }

    @TypeConverter
    fun marketToString(market: CoinPrice?): String {
        return Gson().toJson(market)
    }
}