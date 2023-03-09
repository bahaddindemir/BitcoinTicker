package com.bahaddindemir.bitcointicker.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.databinding.ItemCoinBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter

class CoinViewHolder(private val binding: ItemCoinBinding, private val delegate: Delegate) :
    RecyclerView.ViewHolder(binding.root), BaseAdapter.Binder<CoinItem> {
    interface Delegate {
        fun onItemClick(coinItem: CoinItem, view: View)
    }

    override fun bind(data: CoinItem) {
        binding.apply {
            coinItem = data
            executePendingBindings()
        }

        itemView.setOnClickListener { delegate.onItemClick(data, view = it) }
    }
}
