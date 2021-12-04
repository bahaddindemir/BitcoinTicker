package com.bahaddindemir.bitcointicker.ui.mycoin

import android.view.View
import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.databinding.FragmentMyCoinBinding
import com.bahaddindemir.bitcointicker.ui.adapter.MyCoinAdapter
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.viewholder.MyCoinViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCoinFragment : BaseFragment<FragmentMyCoinBinding>(), MyCoinViewHolder.Delegate {
    private val viewModel: MyCoinViewModel by viewModels()

    private val myCoinAdapter = MyCoinAdapter(this)

    override fun getLayoutId() = R.layout.fragment_my_coin

    override fun setBindingVariables() {
        binding.viewModel = viewModel
        binding.myCoinsRecyclerview.adapter = myCoinAdapter
    }

    override fun onItemClick(coinDetailItem: CoinDetailItem, view: View) {

    }
}