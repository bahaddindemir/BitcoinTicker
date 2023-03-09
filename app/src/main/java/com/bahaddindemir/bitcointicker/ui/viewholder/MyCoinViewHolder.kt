package com.bahaddindemir.bitcointicker.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.databinding.ItemMyCoinBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter

class MyCoinViewHolder(private val binding: ItemMyCoinBinding, private val delegate: Delegate) :
    RecyclerView.ViewHolder(binding.root), BaseAdapter.Binder<CoinDetailItem> {
    interface Delegate {
        fun onItemClick(coinDetailItem: CoinDetailItem, view: View)
    }

    override fun bind(data: CoinDetailItem) {
        binding.apply {
            coinDetailItem = data
            executePendingBindings()
        }

        itemView.setOnClickListener { delegate.onItemClick(data, view = it) }
    }
}