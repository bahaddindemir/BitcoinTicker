package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.google.gson.Gson

class CoinImageConverter {
    @TypeConverter
    fun stringToImage(value: String): CoinImage? {
        return Gson().fromJson(value, CoinImage::class.java)
    }

    @TypeConverter
    fun imageToString(image: CoinImage?): String {
        return Gson().toJson(image)
    }
}