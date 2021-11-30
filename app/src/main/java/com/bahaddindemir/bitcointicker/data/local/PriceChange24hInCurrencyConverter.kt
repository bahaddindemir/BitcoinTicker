package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.PriceChange24hInCurrency
import com.google.gson.Gson

class PriceChange24hInCurrencyConverter {
    @TypeConverter
    fun stringToPriceChange24hInCurrency(value: String): PriceChange24hInCurrency? {
        return Gson().fromJson(value, PriceChange24hInCurrency::class.java)
    }

    @TypeConverter
    fun priceChange24hInCurrencyToString(description: PriceChange24hInCurrency?): String {
        return Gson().toJson(description)
    }
}