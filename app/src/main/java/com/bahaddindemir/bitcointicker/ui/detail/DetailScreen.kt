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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import com.bahaddindemir.bitcointicker.data.model.coin.CurrentPrice
import com.bahaddindemir.bitcointicker.data.model.coin.PriceChange24hInCurrency
import com.bahaddindemir.bitcointicker.ui.auth.AuthViewModel
import com.bahaddindemir.bitcointicker.ui.components.LoadingDialog
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerColors

@Composable
fun DetailRoute(
    coinId: String,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailLoadErrorMessage = stringResource(id = R.string.detail_load_error)
    val favoriteChangeErrorMessage = stringResource(id = R.string.favorite_change_error)

    LaunchedEffect(coinId) {
        viewModel.startRefreshing(coinId)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                DetailUiEvent.DetailLoadFailed -> snackbarHostState.showSnackbar(detailLoadErrorMessage)
                DetailUiEvent.FavoriteChangeFailed -> snackbarHostState.showSnackbar(favoriteChangeErrorMessage)
            }
        }
    }

    DetailScreen(
        title = uiState.title,
        coinDetailItem = uiState.coinDetailItem,
        defaultCurrency = uiState.defaultCurrency,
        intervalText = uiState.intervalText,
        isFavorite = uiState.isFavorite,
        lastUpdatedDate = uiState.lastUpdatedDate,
        onIntervalChange = viewModel::onIntervalChange,
        onConfirmClick = viewModel::onConfirmIntervalClick,
        onFavoriteClick = { viewModel.onFavoriteClick(authViewModel.user) },
        onBackClick = onBackClick,
        modifier = modifier
    )
    LoadingDialog(isVisible = uiState.isLoading)
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
    val colors = BitcoinTickerColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
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
    val colors = BitcoinTickerColors.current

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
                color = colors.onDark,
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
    val colors = BitcoinTickerColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.refresh_interval),
            color = colors.onDark,
            fontSize = 20.sp
        )
        TextField(
            value = intervalText,
            onValueChange = onIntervalChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = colors.onDark,
                unfocusedTextColor = colors.onDark,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colors.onDark,
                unfocusedIndicatorColor = colors.onDark,
                cursorColor = colors.onDark
            )
        )
        Button(
            onClick = onConfirmClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.action,
                contentColor = colors.onDark
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
    val colors = BitcoinTickerColors.current
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
                color = colors.onDark,
                fontSize = 18.sp
            )
            Text(
                text = hashAlgorithm,
                modifier = Modifier.padding(top = 4.dp),
                color = colors.onDark,
                fontSize = 20.sp
            )
        }

        DetailSectionDivider(visible = description.isNotEmpty())
        if (description.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.description),
                modifier = Modifier.padding(top = 16.dp),
                color = colors.onDark,
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
    val colors = BitcoinTickerColors.current

    if (visible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(1.dp)
                .background(colors.background)
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
    val colors = BitcoinTickerColors.current

    if (visible) {
        Column(modifier = modifier) {
            Text(
                text = title,
                modifier = Modifier.padding(top = 16.dp),
                color = colors.onDark,
                fontSize = 18.sp
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = valueTopPadding),
                color = colors.onDark,
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
    val colors = BitcoinTickerColors.current

    Text(
        text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString(),
        modifier = modifier.fillMaxWidth(),
        color = colors.onDark
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
