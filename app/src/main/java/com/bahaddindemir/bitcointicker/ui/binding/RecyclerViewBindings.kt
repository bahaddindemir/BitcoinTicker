package com.bahaddindemir.bitcointicker.ui.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter
import com.bahaddindemir.bitcointicker.ui.adapter.CoinAdapter
import com.bahaddindemir.bitcointicker.ui.adapter.MyCoinAdapter

@BindingAdapter("adapter")
fun bindAdapter(view: RecyclerView, baseAdapter: BaseAdapter) {
    view.adapter = baseAdapter
}

@BindingAdapter("adapterCoin")
fun bindAdapterCoin(view: RecyclerView, resource: CoinResource<List<CoinItem>>?) {
    if (resource != null) {
        val adapter = view.adapter as? CoinAdapter
        adapter?.addCurrencyItemList(resource)
    }
}

@BindingAdapter("adapterMyCoin")
fun bindAdapterMyCoin(view: RecyclerView, resource: CoinResource<List<CoinDetailItem>>?) {
    if (resource != null) {
        val adapter = view.adapter as? MyCoinAdapter
        adapter?.addCurrencyItemList(resource)
    }
}