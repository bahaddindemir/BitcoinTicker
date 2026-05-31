package com.bahaddindemir.bitcointicker.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.extension.isNegative
import com.bahaddindemir.bitcointicker.extension.marketCapToText
import com.bahaddindemir.bitcointicker.extension.priceChangeToText
import com.bahaddindemir.bitcointicker.ui.components.LoadingDialog
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerColors

@Composable
fun HomeRoute(
    snackbarHostState: SnackbarHostState,
    onCoinClick: (CoinItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coinsLoadErrorMessage = stringResource(id = R.string.coins_load_error)

    fun closeSearch() {
        keyboardController?.hide()
        viewModel.onCloseSearchClick()
    }

    BackHandler(enabled = uiState.isSearchVisible) {
        closeSearch()
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                HomeUiEvent.CoinsLoadFailed -> snackbarHostState.showSnackbar(coinsLoadErrorMessage)
            }
        }
    }

    HomeScreen(
        coins = uiState.coins,
        isSearchVisible = uiState.isSearchVisible,
        searchText = uiState.searchText,
        isContentVisible = uiState.isContentVisible,
        onSearchClick = viewModel::onSearchClick,
        onSearchChange = viewModel::onSearchChange,
        onCloseSearchClick = ::closeSearch,
        onCoinClick = onCoinClick,
        modifier = modifier
    )
    LoadingDialog(isVisible = uiState.isLoading)
}

@Composable
fun HomeScreen(
    coins: List<CoinItem>,
    isSearchVisible: Boolean,
    searchText: String,
    isContentVisible: Boolean,
    onSearchClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onCloseSearchClick: () -> Unit,
    onCoinClick: (CoinItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BitcoinTickerColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        HomeToolbar(
            isSearchVisible = isSearchVisible,
            searchText = searchText,
            onSearchClick = onSearchClick,
            onSearchChange = onSearchChange,
            onCloseSearchClick = onCloseSearchClick
        )
        if (isContentVisible) {
            CoinsHeader()
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = coins,
                    key = { coin -> coin.id }
                ) { coin ->
                    CoinRow(
                        coin = coin,
                        onClick = { onCoinClick(coin) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeToolbar(
    isSearchVisible: Boolean,
    searchText: String,
    onSearchClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onCloseSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSearchVisible) {
        SearchToolbar(
            searchText = searchText,
            onSearchChange = onSearchChange,
            onCloseSearchClick = onCloseSearchClick,
            modifier = modifier
        )
    } else {
        DefaultHomeToolbar(
            onSearchClick = onSearchClick,
            modifier = modifier
        )
    }
}

@Composable
private fun SearchToolbar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onCloseSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val colors = BitcoinTickerColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp),
            tint = Color.Unspecified
        )
        BasicTextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .background(
                    color = colors.brand,
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = colors.onDark,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(colors.onDark),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (searchText.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.search),
                            color = colors.onDark.copy(alpha = 0.65f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            }
        )
        IconButton(
            onClick = {
                keyboardController?.hide()
                onCloseSearchClick()
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.cancel),
                modifier = Modifier.padding(8.dp),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun DefaultHomeToolbar(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BitcoinTickerColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fg),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                color = colors.onDark,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(60.dp)
                .padding(end = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = R.string.search),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun CoinsHeader(modifier: Modifier = Modifier) {
    val colors = BitcoinTickerColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.coin),
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
            color = colors.mutedText,
            fontSize = 14.sp
        )
        Text(
            text = stringResource(id = R.string.price),
            modifier = Modifier.padding(end = 32.dp),
            color = colors.mutedText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CoinRow(
    coin: CoinItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BitcoinTickerColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinImage(
            imageUrl = coin.image,
            contentDescription = coin.name,
            modifier = Modifier
                .size(80.dp)
                .padding(16.dp)
        )
        Column(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = coin.name,
                color = colors.onDark,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = coin.symbol.uppercase(),
                color = colors.onDark,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .height(80.dp)
                .padding(end = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = coin.currentPrice.marketCapToText(),
                color = colors.onDark,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = coin.priceChangePercentage24h.priceChangeToText(),
                color = if (coin.priceChangePercentage24h.isNegative()) Color.Red else Color.Green,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CoinImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        error = painterResource(id = R.drawable.ic_fg),
        fallback = painterResource(id = R.drawable.ic_fg),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        coins = listOf(
            CoinItem(
                id = "bitcoin",
                symbol = "btc",
                name = "Bitcoin",
                image = "",
                currentPrice = 44051f,
                priceChangePercentage24h = -1.5603f
            ),
            CoinItem(
                id = "ethereum",
                symbol = "eth",
                name = "Ethereum",
                image = "",
                currentPrice = 2920f,
                priceChangePercentage24h = 2.42f
            )
        ),
        isSearchVisible = false,
        searchText = "",
        isContentVisible = true,
        onSearchClick = {},
        onSearchChange = {},
        onCloseSearchClick = {},
        onCoinClick = {}
    )
}
