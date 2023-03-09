package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import com.bahaddindemir.bitcointicker.extension.toJsonModel
import com.bahaddindemir.bitcointicker.extension.toJsonString

class CoinLocalizationConverter {
    @TypeConverter
    fun stringToDescription(value: String): CoinLocalization {
        return value.toJsonModel(CoinLocalization::class.java)
    }

    @TypeConverter
    fun descriptionToString(description: CoinLocalization): String {
        return description.toJsonString()
    }
}