package com.bahaddindemir.bitcointicker.data.model.coin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class CoinItem(
    @SerializedName("id")
    @PrimaryKey
    val id: String = "",
    @SerializedName("symbol")
    val symbol: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("current_price")
    val currentPrice: Float = 0f,
    @SerializedName("market_cap")
    val marketCap: Float = 0f,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int = 0,
    @SerializedName("total_volume")
    val totalVolume: Float = 0f,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Float = 0f,
    @SerializedName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Float = 0f,
    @SerializedName("circulating_supply")
    val circulatingSupply: Double = 0.0,
    @SerializedName("total_supply")
    val totalSupply: Float = 0f,
    @SerializedName("ath")
    val ath: Float = 0f,
    @SerializedName("ath_change_percentage")
    val athChangePercentage: Float = 0f) : Parcelable