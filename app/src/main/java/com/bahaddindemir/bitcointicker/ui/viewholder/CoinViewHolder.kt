package com.bahaddindemir.bitcointicker.ui.viewholder

import android.view.View
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.databinding.ItemCoinBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseViewHolder
import com.bahaddindemir.bitcointicker.util.bindings

class CoinViewHolder(view: View, private val delegate: Delegate) : BaseViewHolder(view) {
    interface Delegate {
        fun onItemClick(coinItem: CoinItem, view: View)
    }

    private lateinit var coinItem: CoinItem
    private val binding by bindings<ItemCoinBinding>(view)

    override fun bindData(data: Any) {
        if (data is CoinItem) {
            coinItem = data

            binding.apply {
                coinItem = data
                executePendingBindings()
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            delegate.onItemClick(coinItem, view)
        }
    }

    override fun onLongClick(v: View?): Boolean = false
}
