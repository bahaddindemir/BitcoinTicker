package com.bahaddindemir.bitcointicker.ui.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import com.bahaddindemir.bitcointicker.data.model.coin.CoinLocalization
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreenDisplaysCoinDetails() {
        composeTestRule.setContent {
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

        composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refresh Interval").assertIsDisplayed()
        composeTestRule.onNodeWithText("SHA-256").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Updated Date").assertIsDisplayed()
    }
}
