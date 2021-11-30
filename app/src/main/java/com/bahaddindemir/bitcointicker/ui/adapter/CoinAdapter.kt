package com.bahaddindemir.bitcointicker.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter
import com.bahaddindemir.bitcointicker.ui.base.BaseViewHolder
import com.bahaddindemir.bitcointicker.ui.viewholder.CoinViewHolder
import com.bahaddindemir.bitcointicker.util.SectionRow

class CoinAdapter(private val delegate: CoinViewHolder.Delegate) : BaseAdapter() {
    init {
        addSection(ArrayList<CoinItem>())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCurrencyItemList(resource: CoinResource<List<CoinItem>?>) {
        resource.data?.let {
            sections()[0].addAll(it)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(coinList: List<CoinItem>?) {
        coinList?.let {
            sections()[0].clear()
            sections()[0].addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun layout(sectionRow: SectionRow): Int = R.layout.item_coin

    override fun viewHolder(layout: Int, view: View): BaseViewHolder =
        CoinViewHolder(view, delegate)
}