package com.bahaddindemir.bitcointicker.ui.adapter

import android.view.View
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter
import com.bahaddindemir.bitcointicker.ui.base.BaseViewHolder
import com.bahaddindemir.bitcointicker.ui.viewholder.MyCoinViewHolder
import com.bahaddindemir.bitcointicker.util.SectionRow

class MyCoinAdapter(private val delegate: MyCoinViewHolder.Delegate) : BaseAdapter() {
    init {
        addSection(ArrayList<CoinDetailItem>())
    }

    fun addCurrencyItemList(resource: CoinResource<List<CoinDetailItem>?>) {
        resource.data?.let {
            sections()[0].addAll(it)
            notifyDataSetChanged()
        }
    }

    fun setData(currencyList: List<CoinDetailItem>?) {
        currencyList?.let {
            sections()[0].clear()
            sections()[0].addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun layout(sectionRow: SectionRow): Int = R.layout.item_my_coins

    override fun viewHolder(layout: Int, view: View): BaseViewHolder =
        MyCoinViewHolder(view, delegate)
}