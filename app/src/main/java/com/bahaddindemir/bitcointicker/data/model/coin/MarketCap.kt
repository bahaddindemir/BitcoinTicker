package com.bahaddindemir.bitcointicker.data.model.coin

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class MarketCap(
    @SerializedName("aed")
    val aed: Long,
    @SerializedName("ars")
    val ars: Long,
    @SerializedName("aud")
    val aud: Long,
    @SerializedName("bch")
    val bch: Long,
    @SerializedName("bdt")
    val bdt: Long,
    @SerializedName("bhd")
    val bhd: Long,
    @SerializedName("bmd")
    val bmd: Long,
    @SerializedName("bnb")
    val bnb: Long,
    @SerializedName("brl")
    val brl: Long,
    @SerializedName("btc")
    val btc: Long,
    @SerializedName("cad")
    val cad: Long,
    @SerializedName("chf")
    val chf: Long,
    @SerializedName("clp")
    val clp: Long,
    @SerializedName("cny")
    val cny: Long,
    @SerializedName("czk")
    val czk: Long,
    @SerializedName("dkk")
    val dkk: Long,
    @SerializedName("dot")
    val dot: Long,
    @SerializedName("eos")
    val eos: Long,
    @SerializedName("eth")
    val eth: Long,
    @SerializedName("eur")
    val eur: Long,
    @SerializedName("gbp")
    val gbp: Long,
    @SerializedName("hkd")
    val hkd: Long,
    @SerializedName("huf")
    val huf: Long,
    @SerializedName("idr")
    val idr: Long,
    @SerializedName("ils")
    val ils: Long,
    @SerializedName("inr")
    val inr: Long,
    @SerializedName("jpy")
    val jpy: Long,
    @SerializedName("krw")
    val krw: Long,
    @SerializedName("kwd")
    val kwd: Long,
    @SerializedName("lkr")
    val lkr: Long,
    @SerializedName("ltc")
    val ltc: Long,
    @SerializedName("mmk")
    val mmk: Long,
    @SerializedName("mxn")
    val mxn: Long,
    @SerializedName("myr")
    val myr: Long,
    @SerializedName("nok")
    val nok: Long,
    @SerializedName("nzd")
    val nzd: Long,
    @SerializedName("php")
    val php: Long,
    @SerializedName("pkr")
    val pkr: Long,
    @SerializedName("pln")
    val pln: Long,
    @SerializedName("rub")
    val rub: Long,
    @SerializedName("sar")
    val sar: Long,
    @SerializedName("sek")
    val sek: Long,
    @SerializedName("sgd")
    val sgd: Long,
    @SerializedName("thb")
    val thb: Long,
    @SerializedName("try")
    val tryX: Long,
    @SerializedName("twd")
    val twd: Long,
    @SerializedName("uah")
    val uah: Long,
    @SerializedName("usd")
    val usd: Long,
    @SerializedName("vef")
    val vef: Long,
    @SerializedName("vnd")
    val vnd: Long,
    @SerializedName("xag")
    val xag: Long,
    @SerializedName("xau")
    val xau: Long,
    @SerializedName("xdr")
    val xdr: Long,
    @SerializedName("xlm")
    val xlm: Long,
    @SerializedName("xrp")
    val xrp: Long,
    @SerializedName("yfi")
    val yfi: Long,
    @SerializedName("zar")
    val zar: Long,
    @SerializedName("link")
    val link: Long) : Parcelable