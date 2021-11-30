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
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.util.SharedPreferenceHelper
import com.bahaddindemir.bitcointicker.util.setImageWithGlide
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()
    private val authViewMode : AuthViewModel by viewModels()

    override fun getLayoutId() = R.layout.fragment_detail

    private lateinit var coinItem: CoinItem
    private var coinDetailItem: CoinDetailItem? = null
    private var refreshIntervalTime: Long = 2000L

    private var isFavoriteCoin = false

    private var confirmIntervalTime: Long = 0L

    companion object {
        private const val WHAT_MSG = 1
        const val DEFAULT_CURRENCY = "DEFAULT_CURRENCY"
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            loadCoinDetail(coinItem)
        }
    }

    override
    fun setBindingVariables() {
        binding.viewModel = viewModel
    }

    private fun initAndRefreshIntervalTime() {
        val refreshTime: String = refreshIntervalTime.toString()
        binding.refreshInterval.setText(refreshTime)
        binding.refreshInterval.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty()) {
                        confirmIntervalTime = it.trim().toString().toLong()
                    }
                }
            }
        })
    }

    override fun setUpViews() {
        initAndRefreshIntervalTime()
        val bundle = arguments
        bundle?.let {
            coinItem = it.get("coinItem") as CoinItem
            loadCoinDetail(coinItem)
            initializeToolbar(coinItem)
        }
    }

    override fun setupObservers() {
        setClickListeners()
        observeCoinDetailData()
    }

    private fun observeCoinDetailData() {
        viewModel.coinDetailLiveData.observe(viewLifecycleOwner, { resource ->
            when (resource.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    resource?.let {
                        setDetails(it.data)

                        coinDetailItem = it.data

                        it.data?.image?.small?.run {
                            toolbarCenterLogo(this)
                        }

                        it.data?.isFavorite?.run {

                        }
                    }

                    val msg = Message.obtain()
                    msg.what = WHAT_MSG
                    mHandler.sendMessageDelayed(msg, refreshIntervalTime)
                }
                Status.ERROR -> {

                }
            }
        })
    }

    private fun setClickListeners() {
        binding.confirmBtn.setOnClickListener {
            setIntervalTime(if (confirmIntervalTime != 0L) confirmIntervalTime else refreshIntervalTime)
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
            val defaultCurrency =
                SharedPreferenceHelper.getSharedData(DEFAULT_CURRENCY) as? String
            if (!defaultCurrency.isNullOrEmpty()) {
                val currentPriceStr: String = when (defaultCurrency) {
                    "TRY" -> {
                        this.tryX.toString()
                    }
                    "USD" -> {
                        this.usd.toString()
                    }
                    "ETH" -> {
                        this.eth.toString()
                    }
                    else -> {
                        this.btc.toString()
                    }
                }
                val combineString = "$currentPriceStr $defaultCurrency"
                binding.currentPrice.text = combineString
                binding.currentPriceTxt.visibility = View.VISIBLE
                binding.currentPriceLine.visibility = View.VISIBLE
            }
        }
    }

    private fun setPriceChangePercentage24hIn(priceChange24hInCurrency: PriceChange24hInCurrency?) {
        priceChange24hInCurrency?.run {
            var priceChangePercentage24hInStr = ""
            priceChangePercentage24hInStr = when (priceChangePercentage24hInStr) {
                "TRY" -> {
                    this.tryX.toString()
                }
                "USD" -> {
                    this.usd.toString()
                }
                "ETH" -> {
                    this.eth.toString()
                }
                else -> {
                    this.btc.toString()
                }
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
        binding.toolbar.add.setBackgroundResource(R.drawable.splash_background)
        coinItem?.let {
            binding.toolbar.title.text = it.name
        }
        binding.toolbar.add.let {
            it.setOnClickListener {
                coinDetailItem?.run {
                    val firebaseUserData = authViewMode.user?.let { fireBaseUser ->
                        isFavoriteCoin = if (isFavoriteCoin) {
                            binding.toolbar.add.setBackgroundResource(R.drawable.ic_add_fovorite)
                            false
                        } else {
                            binding.toolbar.add.setBackgroundResource(R.drawable.ic_remove_favorite)
                            true
                        }
                        viewModel.onAddFavoriteFireStore(fireBaseUser, this)
                    }
                }
            }
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
}