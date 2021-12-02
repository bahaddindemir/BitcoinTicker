package com.bahaddindemir.bitcointicker.ui.binding

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.di.GlideApp
import com.bahaddindemir.bitcointicker.util.SharedPreferenceHelper

@BindingAdapter("bindingCoinImageUrl")
fun bindingCoinImageUrl(imageView: ImageView, path: String?) {
    path?.let {
        GlideApp.with(imageView.context)
            .load(it)
            .error(ContextCompat.getDrawable(imageView.context, R.drawable.ic_fg))
            .into(imageView)
    }
}

@BindingAdapter("bindingFloatToText")
fun bindingFloatToText(textView: TextView, value: Float) {
    textView.text = value.toString()
}

@BindingAdapter("bindingPriceChangePercentage24hToText")
fun bindingPriceChangePercentage24hToText(textView: TextView, value: Float) {
    val price24HPercentageValue: String = value.toString()
    price24HPercentageValue.let {
        if (it.contains("-")) {
            textView.setTextColor(Color.RED)
        } else {
            textView.setTextColor(Color.GREEN)
        }

        val combineString = if (it.length >= 5) {
            "${price24HPercentageValue.substring(0, 5)} %"
        } else {
            "$price24HPercentageValue %"
        }
        textView.text = combineString
    }
}

@BindingAdapter("bindingMarketCapToText")
fun bindingMarketCapToText(textView: TextView, value: Float) {
    val defaultCurrency = "DEFAULT_CURRENCY"
    val castValue = value.toString()
    var combineString = ""
    var currency = SharedPreferenceHelper.getSharedData(defaultCurrency) as String?
    if (currency == null)   currency = "BTC"
    currency.let {
        combineString = if (castValue.length >= 6) {
            "${castValue.substring(0, 6)} $it"
        } else {
            "$castValue $it"
        }
    }
    textView.text = combineString
}

@BindingAdapter("bindingIntToText")
fun bindingIntToText(textView: TextView, value: Int) {
    textView.text = value.toString()
}