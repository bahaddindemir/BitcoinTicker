package com.bahaddindemir.bitcointicker.ui.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource

@BindingAdapter("hashAlgorithm")
fun bindHashAlgorithm(view: TextView, resource: CoinResource<CoinDetailItem>?) {
    resource?.data?.let {
        it.hashingAlgorithm?.run {
            val hashAlgorithm = "Hashing Algorithm: $this"
            view.text = hashAlgorithm
        }
    }
}