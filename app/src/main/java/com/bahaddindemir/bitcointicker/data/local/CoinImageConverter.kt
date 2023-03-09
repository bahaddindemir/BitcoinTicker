package com.bahaddindemir.bitcointicker.data.local

import androidx.room.TypeConverter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.bahaddindemir.bitcointicker.extension.toJsonModel
import com.bahaddindemir.bitcointicker.extension.toJsonString

class CoinImageConverter {
    @TypeConverter
    fun stringToImage(value: String): CoinImage {
        return value.toJsonModel(CoinImage::class.java)
    }

    @TypeConverter
    fun imageToString(image: CoinImage): String {
        return image.toJsonString()
    }
}