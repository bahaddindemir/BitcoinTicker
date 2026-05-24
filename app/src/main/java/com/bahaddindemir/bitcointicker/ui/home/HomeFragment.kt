package com.bahaddindemir.bitcointicker.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import com.bahaddindemir.bitcointicker.extension.hideKeyboard
import com.bahaddindemir.bitcointicker.extension.hideLoadingDialog
import com.bahaddindemir.bitcointicker.extension.isNegative
import com.bahaddindemir.bitcointicker.extension.marketCapToText
import com.bahaddindemir.bitcointicker.extension.priceChangeToText
import com.bahaddindemir.bitcointicker.extension.showError
import com.bahaddindemir.bitcointicker.extension.showLoadingDialog
import coil3.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private val coins = mutableStateListOf<CoinItem>()
    private var isSearchVisible by mutableStateOf(false)
    private var searchText by mutableStateOf("")
    private var isContentVisible by mutableStateOf(false)
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSearchVisible) {
                    closeSearch()
                } else {
                    isEnabled = false
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HomeScreen(
                    coins = coins,
                    isSearchVisible = isSearchVisible,
                    searchText = searchText,
                    isContentVisible = isContentVisible,
                    onSearchClick = { isSearchVisible = true },
                    onSearchChange = ::onSearchChanged,
                    onCloseSearchClick = ::closeSearch,
                    onCoinClick = { coinItem ->
                        navigateToDetail(coinItem, this)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCoinsMarketsResource()
        loadCoinsMarkets(1)
    }

    override fun onDestroyView() {
        hideLoading()
        super.onDestroyView()
    }

    private fun observeCoinsMarketsResource() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.coinState.collect { resource ->
                        when (resource) {
                            CoinResource.Loading -> {
                                showLoading()
                                isContentVisible = false
                            }

                            is CoinResource.Success -> {
                                hideLoading()
                                coins.replaceAll(resource.data.orEmpty())
                                isContentVisible = true
                            }

                            is CoinResource.Error -> {
                                hideLoading()
                                showError(getString(R.string.some_error))
                            }
                        }
                    }
                }

                launch {
                    viewModel.searchCoinState.collect { result ->
                        coins.replaceAll(result)
                    }
                }
            }
        }
    }

    private fun onSearchChanged(value: String) {
        searchText = value.trim()
        viewModel.postSearchCoinsMarketsPage(searchText)
    }

    private fun closeSearch() {
        hideKeyboard()
        searchText = ""
        isSearchVisible = false
        onSearchChanged("")
    }

    private fun navigateToDetail(coinItem: CoinItem, view: View) {
        val bundle = Bundle().apply {
            putParcelable("coinItem", coinItem)
        }
        view.findNavController().navigate(R.id.detail_fragment, bundle)
    }

    private fun loadCoinsMarkets(page: Int) = viewModel.postCoinsMarketsPage(page)

    private fun showLoading() {
        hideLoading()
        progressDialog = showLoadingDialog()
    }

    private fun hideLoading() = progressDialog.hideLoadingDialog(requireActivity())
}

private fun <T> MutableList<T>.replaceAll(items: List<T>) {
    clear()
    addAll(items)
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.splash_accent))
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
                    color = colorResource(id = R.color.splash),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(Color.White),
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
                            color = Color.White.copy(alpha = 0.65f),
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
                color = Color.White,
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
            color = colorResource(id = R.color.gray),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(id = R.string.price),
            modifier = Modifier.padding(end = 32.dp),
            color = colorResource(id = R.color.gray),
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
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = coin.symbol.uppercase(),
                color = Color.White,
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
                color = Color.White,
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
