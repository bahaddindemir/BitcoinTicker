package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinPrice
import com.bahaddindemir.bitcointicker.extension.toJsonModel
import com.bahaddindemir.bitcointicker.extension.toJsonString

class CoinPriceConverter {
    @TypeConverter
    fun stringToMarket(value: String): CoinPrice {
        return value.toJsonModel(CoinPrice::class.java)
    }

    @TypeConverter
    fun marketToString(market: CoinPrice): String {
        return market.toJsonString()
    }
}