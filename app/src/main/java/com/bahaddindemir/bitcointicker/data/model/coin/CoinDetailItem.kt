package com.bahaddindemir.bitcointicker.data.model.coin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class CoinDetailItem(
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("image")
    val image: CoinImage?,
    @SerializedName("market_data")
    val marketData: CoinPrice?,
    @SerializedName("hashing_algorithm")
    val hashingAlgorithm: String?,
    @SerializedName("description")
    val description: CoinLocalization?,
    var isFavorite: Boolean?) : Parcelable