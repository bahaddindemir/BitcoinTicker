package com.bahaddindemir.bitcointicker.ui.mycoin

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.databinding.FragmentMyCoinBinding
import com.bahaddindemir.bitcointicker.ui.adapter.MyCoinAdapter
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.viewholder.MyCoinViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCoinFragment : BaseFragment<FragmentMyCoinBinding>(), MyCoinViewHolder.Delegate {
    private val viewModel: MyCoinViewModel by viewModels()

    private val myCoinAdapter = MyCoinAdapter(this)

    private lateinit var coinItem: CoinItem

    override fun getLayoutId() = R.layout.fragment_my_coin

    override fun setBindingVariables() {
        binding.viewModel = viewModel
        binding.myCoinsRecyclerview.adapter = myCoinAdapter
    }

    override fun onItemClick(coinDetailItem: CoinDetailItem, view: View) {
        coinItem = CoinItem(coinDetailItem.id)
        val bundle = bundleOf("coinItem" to coinItem)
        view.findNavController().navigate(R.id.detail_fragment, bundle)
    }
}