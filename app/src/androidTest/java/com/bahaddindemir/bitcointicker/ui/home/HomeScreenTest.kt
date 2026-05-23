package com.bahaddindemir.bitcointicker.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.bahaddindemir.bitcointicker.data.model.coin.CoinItem
import com.orhanobut.hawk.Hawk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        Hawk.init(ApplicationProvider.getApplicationContext()).build()
    }

    @Test
    fun homeScreenDisplaysCoinList() {
        composeTestRule.setContent {
            HomeScreen(
                coins = listOf(
                    CoinItem(
                        id = "bitcoin",
                        symbol = "btc",
                        name = "Bitcoin",
                        image = "",
                        currentPrice = 44051f,
                        priceChangePercentage24h = -1.5603f
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

        composeTestRule.onNodeWithText("Bitcoin Ticker").assertIsDisplayed()
        composeTestRule.onNodeWithText("COIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("PRICE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitcoin").assertIsDisplayed()
        composeTestRule.onNodeWithText("BTC").assertIsDisplayed()
    }

    @Test
    fun searchToolbarOpensAndCloses() {
        composeTestRule.setContent {
            var isSearchVisible by remember { mutableStateOf(false) }
            var searchText by remember { mutableStateOf("") }

            HomeScreen(
                coins = emptyList(),
                isSearchVisible = isSearchVisible,
                searchText = searchText,
                isContentVisible = true,
                onSearchClick = { isSearchVisible = true },
                onSearchChange = { searchText = it },
                onCloseSearchClick = {
                    searchText = ""
                    isSearchVisible = false
                },
                onCoinClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()

        composeTestRule.onNodeWithText("Search").performTextInput("bit")
        composeTestRule.onNodeWithText("bit").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        composeTestRule.onNodeWithText("Bitcoin Ticker").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Search").assertIsDisplayed()
    }
}
