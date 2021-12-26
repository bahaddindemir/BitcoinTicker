package com.bahaddindemir.bitcointicker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem

@Database(
    entities = [
        CoinItem::class,
        CoinDetailItem::class
    ], version = 1, exportSchema = false
)

@TypeConverters(
    value = [
        (CoinPriceConverter::class),
        (CoinLocalizationConverter::class),
        (CoinImageConverter::class)
    ]
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
}