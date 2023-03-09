package com.bahaddindemir.bitcointicker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.databinding.ItemMyCoinBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter
import com.bahaddindemir.bitcointicker.ui.viewholder.MyCoinViewHolder

class MyCoinAdapter(private val delegate: MyCoinViewHolder.Delegate) : BaseAdapter<CoinDetailItem>() {
    override fun getLayoutId(position: Int, obj: CoinDetailItem): Int {
        return R.layout.item_my_coin
    }

    override fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder {
        return when (binding) {
            is ItemMyCoinBinding -> MyCoinViewHolder(binding, delegate)
            else -> throw IllegalArgumentException("Unknown ViewBinding")
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding
        get() =
            { inflater, parent, _ -> ItemMyCoinBinding.inflate(inflater, parent, false) }

    fun addCurrencyItemList(resource: List<CoinDetailItem>?) {
        resource?.let { setItems(it) }
    }
}