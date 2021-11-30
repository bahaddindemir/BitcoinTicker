package com.bahaddindemir.bitcointicker.data.model.coin

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinPrice(
    @SerializedName("current_price")
    val currentPrice: CurrentPrice?,
    @SerializedName("market_cap")
    val marketCap: MarketCap,
    @SerializedName("market_cap_rank")
    val marketCapRank: Double,
    @SerializedName("price_change_24h")
    val priceChange24h: Double,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage1y: Double,
    @SerializedName("market_cap_change_24h")
    val marketCapChange24h: Double,
    @SerializedName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double,
    @SerializedName("price_change_24h_in_currency")
    val priceChange24hInCurrency: PriceChange24hInCurrency,
    @SerializedName("price_change_percentage_24h_in_currency")
    val priceChangePercentage24hInCurrency: PriceChangePercentage24hInCurrency?,
    @SerializedName("total_supply")
    val totalSupply: Double,
    @SerializedName("max_supply")
    val maxSupply: Double,
    @SerializedName("circulating_supply")
    val circulatingSupply: Double,
    @SerializedName("last_updated")
    val lastUpdated: String) : Parcelable