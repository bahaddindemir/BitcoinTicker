package com.bahaddindemir.bitcointicker.ui.detail

import android.os.*
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.Status
import com.bahaddindemir.bitcointicker.data.model.coin.*
import com.bahaddindemir.bitcointicker.databinding.FragmentDetailBinding
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.extension.showError
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.bahaddindemir.bitcointicker.util.setImageWithGlide
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var coinItem: CoinItem
    private          var coinDetailItem: CoinDetailItem? = null
    private          var refreshIntervalTime: Long = 2000L

    private var isFavoriteCoin = false

    private var confirmIntervalTime: Long = 0L

    @Inject lateinit var appPreferences: AppPreferences

    companion object {
        private const val WHAT_MSG = 1
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            loadCoinDetail(coinItem)
        }
    }

    override fun getLayoutId() = R.layout.fragment_detail

    override fun getFragmentArguments() {
        super.getFragmentArguments()
        initAndRefreshIntervalTime()
        val bundle = arguments
        bundle?.let {
            coinItem = it.get("coinItem") as CoinItem
            loadCoinDetail(coinItem)
            initializeToolbar(coinItem)
        }
    }

    override
    fun setBindingVariables() {
        binding.viewModel = viewModel
    }

    override fun setupObservers() {
        observeCoinDetailData()
        setClickListeners()
    }

    private fun initAndRefreshIntervalTime() {
        val refreshTime: String = refreshIntervalTime.toString()
        binding.refreshInterval.setText(refreshTime)
        binding.refreshInterval.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    confirmIntervalTime = it.trim().toString().toLong()
                }
            }
        })
    }

    private fun observeCoinDetailData() {
        viewModel.coinDetailLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    showLoading()
                }
                Status.SUCCESS -> {
                    hideLoading()
                    resource.data?.let {
                        handleCoinDetailDataOnSuccess(it)
                    }

                    val msg = Message.obtain()
                    msg.what = WHAT_MSG
                    mHandler.sendMessageDelayed(msg, refreshIntervalTime)
                }
                Status.ERROR -> {
                    hideLoading()
                    showError(getString(R.string.some_error))
                }
            }
        }
    }

    private fun setClickListeners() {
        viewModel.successResponse.observe(this) {
            handleFavoriteButton()
        }
        viewModel.failResponse.observe(this) {
            showError(getString(R.string.some_error))
        }
        binding.confirmBtn.setOnClickListener {
            setIntervalTime(if (confirmIntervalTime != 0L) confirmIntervalTime else refreshIntervalTime)
        }
        binding.toolbar.add.setOnClickListener {
            coinDetailItem?.run {
                authViewModel.user?.let { fireBaseUser ->
                    if (!isFavoriteCoin) {
                        viewModel.onAddFavoriteFireStore(fireBaseUser, this)
                    } else {
                        viewModel.onDeleteFavoriteFireStore(fireBaseUser, this)
                    }
                }
            }
        }
        binding.toolbar.back.setOnClickListener {
            navigateSafe(DetailFragmentDirections.actionOpenHomeFragment())
        }
    }

    private fun setDetails(coinDetailItem: CoinDetailItem?) {
        coinDetailItem?.let {
            setDescription(it.description)
            setCurrentPrice(it.marketData?.currentPrice)
            setPriceChangePercentage24hIn(it.marketData?.priceChange24hInCurrency)
            setHashAlgorithm(it.hashingAlgorithm)
            setRefreshView()
        }
    }

    private fun setHashAlgorithm(hashAlgorithm: String?) {
        hashAlgorithm?.run {
            if (this.isNotEmpty()) {
                binding.hashAlgorithm.text = this
                binding.hashAlgorithmTxt.visibility = View.VISIBLE
            }
        }
    }

    private fun setDescription(description: CoinLocalization?) {
        description?.run {
            if (this.tr.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.description.text = Html.fromHtml(this.tr, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    binding.description.text = Html.fromHtml(this.tr)
                }

                binding.descriptionTxt.visibility = View.VISIBLE
                binding.descriptionLine.visibility = View.VISIBLE
            }
        }
    }

    private fun setCurrentPrice(currentPrice: CurrentPrice?) {
        currentPrice?.run {
            val defaultCurrency = appPreferences.defaultCurrency
            val currentPriceStr: String = when (defaultCurrency) {
                "TRY" -> this.tryX.toString()
                "USD" -> this.usd.toString()
                "ETH" -> this.eth.toString()
                else -> this.btc.toString()
            }
            val combineString = "$currentPriceStr $defaultCurrency"
            binding.currentPrice.text = combineString
            binding.currentPriceTxt.visibility = View.VISIBLE
            binding.currentPriceLine.visibility = View.VISIBLE
        }
    }

    private fun setPriceChangePercentage24hIn(priceChange24hInCurrency: PriceChange24hInCurrency?) {
        priceChange24hInCurrency?.run {
            var priceChangePercentage24hInStr = ""
            priceChangePercentage24hInStr = when (priceChangePercentage24hInStr) {
                "TRY" -> this.tryX.toString()
                "USD" -> this.usd.toString()
                "ETH" -> this.eth.toString()
                else -> this.btc.toString()
            }
            val combineString = "$priceChangePercentage24hInStr %"
            binding.priceChangePercentage24h.text = combineString
            binding.priceChangePercentage24hTxt.visibility = View.VISIBLE
            binding.priceChangePercentage24hLine.visibility = View.VISIBLE
        }
    }

    private fun toolbarCenterLogo(imgUrl: String) {
        binding.toolbar.centerLogo.let {
            setImageWithGlide(it, imgUrl)
        }
    }

    private fun initializeToolbar(coinItem: CoinItem?) {
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.add.visibility = View.VISIBLE
        coinItem?.let {
            binding.toolbar.title.text = it.name
        }
    }

    private fun loadCoinDetail(coinItem: CoinItem?) {
        coinItem?.let {
            viewModel.postCoinDetailId(it.id)
        }
    }

    private fun setIntervalTime(changedTime: Long) {
        refreshIntervalTime = changedTime
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeMessages(WHAT_MSG)
    }

    private fun setRefreshView() {
        binding.refreshDateTxt.visibility = View.VISIBLE
        binding.lastUpdatedLine.visibility = View.VISIBLE
        binding.refreshDate.text = getCurrentDate()
    }

    private fun getCurrentDate(): String {
        val c: Date = Calendar.getInstance().time

        val df = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        return df.format(c)
    }

    private fun handleCoinDetailDataOnSuccess(coinDetailItem: CoinDetailItem) {
        setDetails(coinDetailItem)

        this.coinDetailItem = coinDetailItem

        coinDetailItem.image?.small?.run {
            toolbarCenterLogo(this)
        }
        coinDetailItem.isFavorite?.run {
            isFavoriteCoin = this
            if (isFavoriteCoin) {
                binding.toolbar.add.setImageResource(R.drawable.ic_add_fovorite)
            }
        }
    }

    private fun handleFavoriteButton() {
        if (isFavoriteCoin) {
            binding.toolbar.add.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.toolbar.add.setImageResource(R.drawable.ic_add_fovorite)
        }
        isFavoriteCoin = !isFavoriteCoin

        coinDetailItem?.run {
            this.isFavorite = isFavoriteCoin
            viewModel.updateFavoriteCoinDetail(this)
        }
    }
}