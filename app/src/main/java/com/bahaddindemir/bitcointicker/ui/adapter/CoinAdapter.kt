package com.bahaddindemir.bitcointicker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.databinding.ItemCoinBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter
import com.bahaddindemir.bitcointicker.ui.viewholder.CoinViewHolder

class CoinAdapter(private val delegate: CoinViewHolder.Delegate) : BaseAdapter<CoinItem>() {
    override fun getLayoutId(position: Int, obj: CoinItem): Int {
        return R.layout.item_coin
    }

    override fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder {
        return when (binding) {
            is ItemCoinBinding -> CoinViewHolder(binding, delegate)
            else -> throw IllegalArgumentException("Unknown ViewBinding")
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding
        get() =
            { inflater, parent, _ -> ItemCoinBinding.inflate(inflater, parent, false) }

    fun addCurrencyItemList(resource: CoinResource<List<CoinItem>?>) {
        resource.data?.let { setItems(it) }
    }

    fun setData(coinList: List<CoinItem>?) {
        coinList?.let { setItems(it) }
    }
}