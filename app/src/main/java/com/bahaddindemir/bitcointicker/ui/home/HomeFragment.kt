package com.bahaddindemir.bitcointicker.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.FragmentHomeBinding
import com.bahaddindemir.bitcointicker.data.model.Status
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.extension.hideKeyboard
import com.bahaddindemir.bitcointicker.extension.showError
import com.bahaddindemir.bitcointicker.ui.adapter.CoinAdapter
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.viewholder.CoinViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), CoinViewHolder.Delegate {
    private val viewModel: HomeViewModel by viewModels()
    private var coinAdapter = CoinAdapter(this)

    override fun getLayoutId() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.toolbar.searchView.visibility == View.VISIBLE) {
                    binding.toolbar.closeBtn.performClick()
                } else {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        })
    }

    override fun setBindingVariables() {
        binding.viewModel = viewModel
        binding.coinsRecycler.adapter = coinAdapter
    }

    override fun setUpViews() {
        initializeUI()
        loadCoinsMarkets(1)
    }

    override fun setupObservers() {
        setClickListeners()
        setSearchTextListeners()
        observeCoinsMarketsResource()
    }

    private fun setSearchTextListeners() {
        binding.toolbar.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val searchKey: String = s.toString().trim()
                if (searchKey.isNotEmpty()) {
                    searchCoinsMarkets(searchKey).observe(viewLifecycleOwner) {
                        it?.run {
                            coinAdapter.setData(this)
                        }
                    }
                } else {
                    searchFullCoinsMarkets().observe(viewLifecycleOwner) {
                        it?.run {
                            coinAdapter.setData(this)
                        }
                    }
                }
            }
        })
    }

    private fun setClickListeners() {
        binding.toolbar.search.setOnClickListener {
            binding.toolbar.searchView.visibility = View.VISIBLE
            binding.toolbar.toolbarContainer.visibility = View.GONE
        }

        binding.toolbar.closeBtn.setOnClickListener {
            hideKeyboard()
            binding.toolbar.txtSearch.text.clear()
            binding.toolbar.searchView.visibility = View.GONE
            binding.toolbar.toolbarContainer.visibility = View.VISIBLE
        }
    }

    private fun initializeUI() {
        setToolbarItemsVisibility()
    }

    private fun setToolbarItemsVisibility() {
        binding.toolbar.search.visibility = View.VISIBLE
        binding.toolbar.centerLogo.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_fg)
    }

    private fun observeCoinsMarketsResource() {
        viewModel.coinLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    showLoading()
                    binding.tableCoins.visibility = View.GONE
                    binding.coinsRecycler.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    hideLoading()
                    binding.tableCoins.visibility = View.VISIBLE
                    binding.coinsRecycler.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    hideLoading()
                    showError(getString(R.string.some_error))
                }
            }
        }
    }

    private fun searchCoinsMarkets(searchKey: String): LiveData<List<CoinItem>> {
        return this.viewModel.postSearchCoinsMarketsPage(searchKey)
    }

    private fun searchFullCoinsMarkets(): LiveData<List<CoinItem>> {
        return this.viewModel.postSearchFullCoinsMarketsPage()
    }

    private fun loadCoinsMarkets(page: Int) = this.viewModel.postCoinsMarketsPage(page)

    override fun onItemClick(coinItem: CoinItem, view: View) {
        val bundle = bundleOf("coinItem" to coinItem)
        view.findNavController().navigate(R.id.detail_fragment, bundle)
    }
}