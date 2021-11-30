package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import com.google.gson.Gson

class CoinLocalizationConverter {
    @TypeConverter
    fun stringToDescription(value: String): CoinLocalization? {
        return Gson().fromJson(value, CoinLocalization::class.java)
    }

    @TypeConverter
    fun descriptionToString(description: CoinLocalization?): String {
        return Gson().toJson(description)
    }
}