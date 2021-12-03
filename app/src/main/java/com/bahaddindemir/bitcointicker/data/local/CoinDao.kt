package com.bahaddindemir.bitcointicker.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(coinsMarkets: List<CoinItem>)

    @Query("SELECT * FROM CoinItem")
    fun getCoins(): LiveData<List<CoinItem>>

    @Query("SELECT * FROM CoinItem WHERE name LIKE '%' || :searchKey || '%' OR '%' || :searchKey || '%'")
    fun searchCoins(searchKey: String): LiveData<List<CoinItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCoinDetail(coinDetailItem: CoinDetailItem)

    @Query("SELECT * FROM CoinDetailItem WHERE id = :coinItemId")
    fun getCoinDetail(coinItemId: String): LiveData<CoinDetailItem>
}