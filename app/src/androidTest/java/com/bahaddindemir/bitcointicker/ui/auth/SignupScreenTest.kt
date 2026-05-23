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

class SignupScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signupScreenDisplaysForm() {
        composeTestRule.setContent {
            SignupScreen(
                initialEmail = "",
                initialPassword = "",
                focusTarget = null,
                onFocusHandled = {},
                onEmailChange = {},
                onPasswordChange = {},
                onSignupClick = {}
            )
        }

        composeTestRule.onNodeWithText("REGISTER").assertIsDisplayed()
        composeTestRule.onNodeWithText("EMAIL ADDRESS").assertIsDisplayed()
        composeTestRule.onNodeWithText("PASSWORD").assertIsDisplayed()
        composeTestRule.onNodeWithText("SIGN UP").assertIsDisplayed()
    }

    @Test
    fun signupScreenUpdatesInputsAndHandlesClick() {
        var email = ""
        var password = ""
        var signupClicked = false

        composeTestRule.setContent {
            SignupScreen(
                initialEmail = "",
                initialPassword = "",
                focusTarget = null,
                onFocusHandled = {},
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onSignupClick = { signupClicked = true }
            )
        }

        composeTestRule.onNodeWithTag(SIGNUP_EMAIL_FIELD_TAG).performTextInput("user@example.com")
        composeTestRule.onNodeWithTag(SIGNUP_PASSWORD_FIELD_TAG).performTextInput("secret")
        composeTestRule.onNodeWithText("SIGN UP").performClick()

        assertEquals("user@example.com", email)
        assertEquals("secret", password)
        assertTrue(signupClicked)
    }
}
