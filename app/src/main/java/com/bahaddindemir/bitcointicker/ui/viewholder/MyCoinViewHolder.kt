package com.bahaddindemir.bitcointicker.ui.viewholder

import android.view.View
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.databinding.ItemMyCoinBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseViewHolder
import com.bahaddindemir.bitcointicker.util.bindings

class MyCoinViewHolder(view: View, private val delegate: Delegate) : BaseViewHolder(view) {
    interface Delegate {
        fun onItemClick(coinDetailItem: CoinDetailItem, view: View)
    }

    private lateinit var coinDetailItem: CoinDetailItem
    private val binding by bindings<ItemMyCoinBinding>(view)

    override fun bindData(data: Any) {
        if (data is CoinDetailItem) {
            coinDetailItem = data

            binding.apply {
                coinDetailItem = data
                executePendingBindings()
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            delegate.onItemClick(coinDetailItem, view)
        }
    }

    override fun onLongClick(v: View?): Boolean = false
}