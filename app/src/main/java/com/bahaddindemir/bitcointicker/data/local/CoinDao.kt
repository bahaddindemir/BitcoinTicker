package com.bahaddindemir.bitcointicker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(coinsMarkets: List<CoinItem>)

    @Query("SELECT * FROM CoinItem")
    fun getCoins(): Flow<List<CoinItem>>

    @Query("SELECT * FROM CoinItem WHERE name LIKE '%' || :searchKey || '%' OR '%' || :searchKey || '%'")
    fun searchCoins(searchKey: String): Flow<List<CoinItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCoinDetail(coinDetailItem: CoinDetailItem)

    @Query("SELECT * FROM CoinDetailItem WHERE id = :coinItemId")
    fun getCoinDetail(coinItemId: String): Flow<CoinDetailItem>

    @Query("SELECT * FROM CoinDetailItem WHERE isFavorite = 1")
    fun getFavoriteCoins(): Flow<List<CoinDetailItem>>
}