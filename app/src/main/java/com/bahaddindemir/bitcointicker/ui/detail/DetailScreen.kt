package com.bahaddindemir.bitcointicker.ui.detail

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.data.model.coin.CurrentPrice
import com.bahaddindemir.bitcointicker.data.model.coin.PriceChange24hInCurrency
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.components.LoadingDialog
import coil3.compose.AsyncImage
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DetailRoute(
    coinId: String,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var refreshIntervalTime by remember { mutableLongStateOf(2000L) }
    var confirmIntervalTime by remember { mutableLongStateOf(0L) }
    var coinDetailItem by remember { mutableStateOf<CoinDetailItem?>(null) }
    var coinTitle by remember { mutableStateOf("") }
    var intervalText by remember { mutableStateOf(refreshIntervalTime.toString()) }
    var isFavoriteCoin by remember { mutableStateOf(false) }
    var lastUpdatedDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val someErrorMessage = stringResource(id = R.string.some_error)

    LaunchedEffect(coinId, refreshIntervalTime) {
        while (true) {
            viewModel.setCoinDetailId(coinId)
            delay(refreshIntervalTime)
        }
    }

    LaunchedEffect(viewModel) {
        launch {
            viewModel.coinDetailState.collect { resource ->
                when (resource) {
                    CoinResource.Loading -> {
                        isLoading = true
                    }

                    is CoinResource.Success -> {
                        isLoading = false
                        resource.data?.let { detail ->
                            coinDetailItem = detail
                            coinTitle = detail.name.orEmpty()
                            lastUpdatedDate = getCurrentDate()
                            detail.isFavorite?.let { isFavoriteCoin = it }
                        }
                    }

                    is CoinResource.Error -> {
                        isLoading = false
                        snackbarHostState.showSnackbar(someErrorMessage)
                    }
                }
            }
        }

        launch {
            viewModel.successResponse.collect { isSuccess ->
                if (isSuccess) {
                    isFavoriteCoin = !isFavoriteCoin
                    coinDetailItem?.let { detail ->
                        detail.isFavorite = isFavoriteCoin
                        viewModel.updateFavoriteCoinDetail(detail)
                    }
                } else {
                    snackbarHostState.showSnackbar(someErrorMessage)
                }
            }
        }
    }

    DetailScreen(
        title = coinTitle,
        coinDetailItem = coinDetailItem,
        defaultCurrency = viewModel.defaultCurrency,
        intervalText = intervalText,
        isFavorite = isFavoriteCoin,
        lastUpdatedDate = lastUpdatedDate,
        onIntervalChange = { value ->
            intervalText = value
            confirmIntervalTime = value.trim().toLongOrNull() ?: 0L
        },
        onConfirmClick = {
            refreshIntervalTime = confirmIntervalTime.takeIf { it != 0L } ?: refreshIntervalTime
        },
        onFavoriteClick = {
            coinDetailItem?.let { detail ->
                authViewModel.user?.let { fireBaseUser ->
                    if (!isFavoriteCoin) {
                        viewModel.onAddFavoriteFireStore(fireBaseUser, detail)
                    } else {
                        viewModel.onDeleteFavoriteFireStore(fireBaseUser, detail)
                    }
                }
            }
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
    LoadingDialog(isVisible = isLoading)
}

private fun getCurrentDate(): String {
    val currentTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentTime)
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
    Text(
        text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString(),
        modifier = modifier.fillMaxWidth(),
        color = Color.White
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
