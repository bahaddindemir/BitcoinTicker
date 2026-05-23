package com.bahaddindemir.bitcointicker.ui.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class SplashScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun splashScreenDisplaysBranding() {
        composeTestRule.setContent {
            SplashScreen()
        }

        composeTestRule.onNodeWithText("BT").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitcoin Ticker").assertIsDisplayed()
    }
}
