package com.bahaddindemir.bitcointicker.data.model.coin

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinLocalization(
    @SerializedName("en")
    val en: String?,
    @SerializedName("de")
    val de: String?,
    @SerializedName("es")
    val es: String?,
    @SerializedName("fr")
    val fr: String?,
    @SerializedName("it")
    val it: String?,
    @SerializedName("pl")
    val pl: String?,
    @SerializedName("ro")
    val ro: String?,
    @SerializedName("hu")
    val hu: String?,
    @SerializedName("nl")
    val nl: String?,
    @SerializedName("pt")
    val pt: String?,
    @SerializedName("sv")
    val sv: String?,
    @SerializedName("vi")
    val vi: String?,
    @SerializedName("tr")
    val tr: String,
    @SerializedName("ru")
    val ru: String?,
    @SerializedName("ja")
    val ja: String?,
    @SerializedName("zh")
    val zh: String?,
    @SerializedName("zh-tw")
    val zhTw: String?,
    @SerializedName("ko")
    val ko: String?,
    @SerializedName("ar")
    val ar: String?,
    @SerializedName("th")
    val th: String?,
    @SerializedName("id")
    val id: String?) : Parcelable