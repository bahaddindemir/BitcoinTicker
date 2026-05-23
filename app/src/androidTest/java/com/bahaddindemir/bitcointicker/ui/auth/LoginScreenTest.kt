package com.bahaddindemir.bitcointicker.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreenDisplaysForm() {
        composeTestRule.setContent {
            LoginScreen(
                initialEmail = "",
                initialPassword = "",
                focusTarget = null,
                onFocusHandled = {},
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onSignupClick = {}
            )
        }

        composeTestRule.onNodeWithText("WELCOME").assertIsDisplayed()
        composeTestRule.onNodeWithText("EMAIL ADDRESS").assertIsDisplayed()
        composeTestRule.onNodeWithText("PASSWORD").assertIsDisplayed()
        composeTestRule.onNodeWithText("LOG IN").assertIsDisplayed()
        composeTestRule.onNodeWithText("OR CLICK FOR SING UP").assertIsDisplayed()
    }

    @Test
    fun loginScreenUpdatesInputsAndHandlesClicks() {
        var email = ""
        var password = ""
        var loginClicked = false
        var signupClicked = false

        composeTestRule.setContent {
            LoginScreen(
                initialEmail = "",
                initialPassword = "",
                focusTarget = null,
                onFocusHandled = {},
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLoginClick = { loginClicked = true },
                onSignupClick = { signupClicked = true }
            )
        }

        composeTestRule.onNodeWithTag(LOGIN_EMAIL_FIELD_TAG).performTextInput("user@example.com")
        composeTestRule.onNodeWithTag(LOGIN_PASSWORD_FIELD_TAG).performTextInput("secret")
        composeTestRule.onNodeWithText("LOG IN").performClick()
        composeTestRule.onNodeWithText("OR CLICK FOR SING UP").performClick()

        assertEquals("user@example.com", email)
        assertEquals("secret", password)
        assertTrue(loginClicked)
        assertTrue(signupClicked)
    }
}
