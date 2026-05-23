package com.bahaddindemir.bitcointicker.ui.mycoin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.bahaddindemir.bitcointicker.extension.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyCoinFragment : Fragment() {
    private val viewModel: MyCoinViewModel by viewModels()

    private val myCoins = mutableStateListOf<CoinDetailItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyCoinScreen(
                    coins = myCoins,
                    onCoinClick = { coinDetailItem ->
                        navigateToDetail(coinDetailItem, this)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.coinState.collect { resource ->
                    myCoins.clear()
                    myCoins.addAll(resource)
                }
            }
        }
    }

    private fun navigateToDetail(coinDetailItem: CoinDetailItem, view: View) {
        val coinItem = CoinItem(coinDetailItem.id)
        val bundle = Bundle().apply {
            putParcelable("coinItem", coinItem)
        }

        view.findNavController().navigate(R.id.detail_fragment, bundle)
    }
}

@Composable
fun MyCoinScreen(
    coins: List<CoinDetailItem>,
    onCoinClick: (CoinDetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.splash_accent))
    ) {
        MyCoinToolbar()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = coins,
                key = { coin -> coin.id }
            ) { coin ->
                MyCoinCard(
                    coin = coin,
                    onClick = { onCoinClick(coin) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MyCoinToolbar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.my_coins_fragment),
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MyCoinCard(
    coin: CoinDetailItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.padding(horizontal = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.splash_accent)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoinImage(
                imageUrl = coin.image?.small,
                contentDescription = coin.name
            )
            Text(
                text = coin.name.orEmpty(),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CoinImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.size(48.dp),
        factory = {
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        },
        update = { imageView ->
            imageView.contentDescription = contentDescription
            imageView.loadImage(imageUrl)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MyCoinScreenPreview() {
    MyCoinScreen(
        coins = listOf(
            CoinDetailItem(
                id = "bitcoin",
                symbol = "btc",
                name = "Bitcoin",
                image = CoinImage(
                    thumb = "",
                    small = "",
                    large = ""
                ),
                marketData = null,
                hashingAlgorithm = null,
                description = null,
                isFavorite = true
            ),
            CoinDetailItem(
                id = "ethereum",
                symbol = "eth",
                name = "Ethereum",
                image = CoinImage(
                    thumb = "",
                    small = "",
                    large = ""
                ),
                marketData = null,
                hashingAlgorithm = null,
                description = null,
                isFavorite = true
            )
        ),
        onCoinClick = {}
    )
}
