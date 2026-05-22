package com.bahaddindemir.bitcointicker.ui.mycoin

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.bahaddindemir.bitcointicker.data.model.coin.CoinDetailItem
import com.bahaddindemir.bitcointicker.data.model.coin.CoinImage
import org.junit.Rule
import org.junit.Test

class MyCoinScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myCoinScreenDisplaysTitleAndCoins() {
        composeTestRule.setContent {
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
                    )
                ),
                onCoinClick = {}
            )
        }

        composeTestRule.onNodeWithText("My Coins").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed()
    }
}
