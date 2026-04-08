package com.bahaddindemir.bitcointicker.ui.viewholder

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.databinding.ItemCoinBinding
import com.bahaddindemir.bitcointicker.extension.isNegative
import com.bahaddindemir.bitcointicker.extension.loadImage
import com.bahaddindemir.bitcointicker.extension.marketCapToText
import com.bahaddindemir.bitcointicker.extension.priceChangeToText
import com.bahaddindemir.bitcointicker.ui.base.BaseAdapter

class CoinViewHolder(private val binding: ItemCoinBinding, private val delegate: Delegate) :
    RecyclerView.ViewHolder(binding.root), BaseAdapter.Binder<CoinItem> {
    interface Delegate {
        fun onItemClick(coinItem: CoinItem, view: View)
    }

    override fun bind(data: CoinItem) {
        binding.apply {
            coinImage.loadImage(data.image)
            name.text = data.name
            symbol.text = data.symbol.uppercase()
            currentPrice.text = data.currentPrice.marketCapToText()
            priceChangePercentage24h.setTextColor(
                if (data.priceChangePercentage24h.isNegative()) Color.RED else Color.GREEN
            )
            data.priceChangePercentage24h.priceChangeToText()
        }

        itemView.setOnClickListener { delegate.onItemClick(data, view = it) }
    }
}
