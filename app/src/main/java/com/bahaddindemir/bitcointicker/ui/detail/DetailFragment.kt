package com.bahaddindemir.bitcointicker.ui.detail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.model.coin.CurrentPrice
import com.bahaddindemir.bitcointicker.data.model.coin.PriceChange24hInCurrency
import com.bahaddindemir.bitcointicker.extension.parcelable
import com.bahaddindemir.bitcointicker.extension.showError
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.components.LoadingDialog
import com.bahaddindemir.bitcointicker.util.AppPreferences
import coil3.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var coinItem: CoinItem
    private var refreshIntervalTime: Long = 2000L
    private var confirmIntervalTime: Long = 0L

    private var coinDetailItem by mutableStateOf<CoinDetailItem?>(null)
    private var coinTitle by mutableStateOf("")
    private var intervalText by mutableStateOf(refreshIntervalTime.toString())
    private var isFavoriteCoin by mutableStateOf(false)
    private var lastUpdatedDate by mutableStateOf("")
    private var isLoading by mutableStateOf(false)

    @Inject
    lateinit var appPreferences: AppPreferences

    companion object {
        private const val WHAT_MSG = 1
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            loadCoinDetail(coinItem)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DetailScreen(
                    title = coinTitle,
                    coinDetailItem = coinDetailItem,
                    defaultCurrency = appPreferences.defaultCurrency ?: "BTC",
                    intervalText = intervalText,
                    isFavorite = isFavoriteCoin,
                    lastUpdatedDate = lastUpdatedDate,
                    onIntervalChange = ::onIntervalChanged,
                    onConfirmClick = {
                        setIntervalTime(confirmIntervalTime.takeIf { it != 0L }
                            ?: refreshIntervalTime)
                    },
                    onFavoriteClick = ::onFavoriteClick,
                    onBackClick = {
                        findNavController().popBackStack()
                    }
                )
                LoadingDialog(isVisible = isLoading)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFragmentArguments()
        observeCoinDetailData()
        observeFavoriteResponse()
    }

    private fun getFragmentArguments() {
        arguments?.let {
            coinItem = it.parcelable<CoinItem>("coinItem") ?: return
            coinTitle = coinItem.name
            loadCoinDetail(coinItem)
        }
    }

    private fun observeCoinDetailData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.coinDetailState.collect { resource ->
                    when (resource) {
                        CoinResource.Loading -> showLoading()
                        is CoinResource.Success -> {
                            hideLoading()
                            resource.data?.let {
                                handleCoinDetailDataOnSuccess(it)
                            }

                            val msg = Message.obtain()
                            msg.what = WHAT_MSG
                            handler.sendMessageDelayed(msg, refreshIntervalTime)
                        }

                        is CoinResource.Error -> {
                            hideLoading()
                            showError(getString(R.string.some_error))
                        }
                    }
                }
            }
        }
    }

    private fun observeFavoriteResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.successResponse.collect {
                    if (it) handleFavoriteButton()
                    else showError(getString(R.string.some_error))
                }
            }
        }
    }

    private fun onIntervalChanged(value: String) {
        intervalText = value
        confirmIntervalTime = value.trim().toLongOrNull() ?: 0L
    }

    private fun onFavoriteClick() {
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

    private fun loadCoinDetail(coinItem: CoinItem?) {
        coinItem?.let {
            viewModel.setCoinDetailId(it.id)
        }
    }

    private fun setIntervalTime(changedTime: Long) {
        refreshIntervalTime = changedTime
    }

    override fun onDestroyView() {
        handler.removeMessages(WHAT_MSG)
        hideLoading()
        super.onDestroyView()
    }

    private fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    private fun handleCoinDetailDataOnSuccess(coinDetailItem: CoinDetailItem) {
        this.coinDetailItem = coinDetailItem
        lastUpdatedDate = getCurrentDate()
        coinDetailItem.isFavorite?.let {
            isFavoriteCoin = it
        }
    }

    private fun handleFavoriteButton() {
        isFavoriteCoin = !isFavoriteCoin

        coinDetailItem?.run {
            this.isFavorite = isFavoriteCoin
            viewModel.updateFavoriteCoinDetail(this)
        }
    }

    private fun showLoading() {
        isLoading = true
    }

    private fun hideLoading() {
        isLoading = false
    }
}

@Composable
fun DetailScreen(
    title: String,
    coinDetailItem: CoinDetailItem?,
    defaultCurrency: String,
    intervalText: String,
    isFavorite: Boolean,
    lastUpdatedDate: String,
    onIntervalChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.splash_accent))
    ) {
        DetailToolbar(
            title = title,
            logoUrl = coinDetailItem?.image?.small,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onBackClick = onBackClick
        )
        RefreshIntervalContent(
            intervalText = intervalText,
            onIntervalChange = onIntervalChange,
            onConfirmClick = onConfirmClick
        )
        DetailContent(
            coinDetailItem = coinDetailItem,
            defaultCurrency = defaultCurrency,
            lastUpdatedDate = lastUpdatedDate,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetailToolbar(
    title: String,
    logoUrl: String?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(60.dp)
                .padding(start = 8.dp),
            onClick = onBackClick
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 68.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoinLogo(imageUrl = logoUrl)
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(60.dp)
                .padding(end = 8.dp),
            onClick = onFavoriteClick
        ) {
            Image(
                painter = painterResource(
                    id = if (isFavorite) R.drawable.ic_add_fovorite else R.drawable.ic_favorite
                ),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CoinLogo(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier.size(36.dp),
        error = painterResource(id = R.drawable.ic_fg),
        fallback = painterResource(id = R.drawable.ic_fg),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun RefreshIntervalContent(
    intervalText: String,
    onIntervalChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.refresh_interval),
            color = Color.White,
            fontSize = 20.sp
        )
        TextField(
            value = intervalText,
            onValueChange = onIntervalChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                cursorColor = Color.White
            )
        )
        Button(
            onClick = onConfirmClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.button_background),
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.confirm_btn))
        }
    }
}

@Composable
private fun DetailContent(
    coinDetailItem: CoinDetailItem?,
    defaultCurrency: String,
    lastUpdatedDate: String,
    modifier: Modifier = Modifier
) {
    val hashAlgorithm = coinDetailItem?.hashingAlgorithm.orEmpty()
    val description = coinDetailItem?.description?.tr.orEmpty()
    val currentPrice = coinDetailItem?.marketData?.currentPrice?.format(defaultCurrency).orEmpty()
    val priceChange24h = coinDetailItem?.marketData?.priceChange24hInCurrency
        ?.format(defaultCurrency)
        .orEmpty()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp)
    ) {
        if (hashAlgorithm.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.hash_algorithm),
                modifier = Modifier.padding(top = 36.dp),
                color = Color.White,
                fontSize = 18.sp
            )
            Text(
                text = hashAlgorithm,
                modifier = Modifier.padding(top = 4.dp),
                color = Color.White,
                fontSize = 20.sp
            )
        }

        DetailSectionDivider(visible = description.isNotEmpty())
        if (description.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.description),
                modifier = Modifier.padding(top = 16.dp),
                color = Color.White,
                fontSize = 18.sp
            )
            HtmlText(
                html = description.take(500),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        DetailSectionDivider(visible = currentPrice.isNotEmpty())
        DetailTextSection(
            visible = currentPrice.isNotEmpty(),
            title = stringResource(id = R.string.current_price),
            value = currentPrice
        )

        DetailSectionDivider(visible = priceChange24h.isNotEmpty())
        DetailTextSection(
            visible = priceChange24h.isNotEmpty(),
            title = stringResource(id = R.string.price_change_percentage_24h),
            value = priceChange24h
        )

        DetailSectionDivider(visible = lastUpdatedDate.isNotEmpty())
        DetailTextSection(
            visible = lastUpdatedDate.isNotEmpty(),
            title = stringResource(id = R.string.detail_up_to_date),
            value = lastUpdatedDate,
            valueTopPadding = 16.dp,
            valueFontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailSectionDivider(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(1.dp)
                .background(colorResource(id = R.color.splash_accent))
        )
    }
}

@Composable
private fun DetailTextSection(
    visible: Boolean,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueTopPadding: androidx.compose.ui.unit.Dp = 4.dp,
    valueFontSize: androidx.compose.ui.unit.TextUnit = 14.sp
) {
    if (visible) {
        Column(modifier = modifier) {
            Text(
                text = title,
                modifier = Modifier.padding(top = 16.dp),
                color = Color.White,
                fontSize = 18.sp
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = valueTopPadding),
                color = Color.White,
                fontSize = valueFontSize
            )
        }
    }
}

@Composable
private fun HtmlText(
    html: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
            TextView(context).apply {
                setTextColor(android.graphics.Color.WHITE)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}

private fun CurrentPrice.format(defaultCurrency: String): String {
    val currentPrice = when (defaultCurrency) {
        "TRY" -> tryX
        "USD" -> usd
        "ETH" -> eth
        else -> btc
    }
    return "$currentPrice $defaultCurrency"
}

private fun PriceChange24hInCurrency.format(defaultCurrency: String): String {
    val priceChange = when (defaultCurrency) {
        "TRY" -> tryX
        "USD" -> usd
        "ETH" -> eth
        else -> btc
    }
    return "$priceChange %"
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    DetailScreen(
        title = "Bitcoin",
        coinDetailItem = CoinDetailItem(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = CoinImage(
                thumb = "",
                small = "",
                large = ""
            ),
            marketData = null,
            hashingAlgorithm = "SHA-256",
            description = CoinLocalization(
                en = null,
                de = null,
                es = null,
                fr = null,
                it = null,
                pl = null,
                ro = null,
                hu = null,
                nl = null,
                pt = null,
                sv = null,
                vi = null,
                tr = "Bitcoin merkezi olmayan bir dijital para birimidir.",
                ru = null,
                ja = null,
                zh = null,
                zhTw = null,
                ko = null,
                ar = null,
                th = null,
                id = null
            ),
            isFavorite = true
        ),
        defaultCurrency = "USD",
        intervalText = "2000",
        isFavorite = true,
        lastUpdatedDate = "23-May-2026 12:00:00",
        onIntervalChange = {},
        onConfirmClick = {},
        onFavoriteClick = {},
        onBackClick = {}
    )
}
