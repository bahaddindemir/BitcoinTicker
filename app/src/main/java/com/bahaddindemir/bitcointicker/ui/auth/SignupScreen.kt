package com.bahaddindemir.bitcointicker.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerColors

enum class SignupFocusTarget {
    Email,
    Password
}

const val SIGNUP_EMAIL_FIELD_TAG = "signup_email_field"
const val SIGNUP_PASSWORD_FIELD_TAG = "signup_password_field"

@Composable
fun SignupScreen(
    initialEmail: String,
    initialPassword: String,
    focusTarget: SignupFocusTarget?,
    onFocusHandled: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val colors = BitcoinTickerColors.current

    LaunchedEffect(focusTarget) {
        when (focusTarget) {
            SignupFocusTarget.Email -> emailFocusRequester.requestFocus()
            SignupFocusTarget.Password -> passwordFocusRequester.requestFocus()
            null -> return@LaunchedEffect
        }
        keyboardController?.show()
        onFocusHandled()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.brand, colors.background)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = stringResource(id = R.string.register).uppercase(),
            color = colors.onDark,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        SignupFieldLabel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 50.dp),
            text = stringResource(id = R.string.email).uppercase()
        )
        SignupTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                .testTag(SIGNUP_EMAIL_FIELD_TAG)
                .focusRequester(emailFocusRequester),
            value = email,
            hint = stringResource(id = R.string.email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onValueChange = {
                email = it
                onEmailChange(it)
            },
            onNext = { passwordFocusRequester.requestFocus() }
        )

        SignupFieldLabel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 50.dp),
            text = stringResource(id = R.string.password).uppercase()
        )
        SignupTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                .testTag(SIGNUP_PASSWORD_FIELD_TAG)
                .focusRequester(passwordFocusRequester),
            value = password,
            hint = stringResource(id = R.string.password),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {
                password = it
                onPasswordChange(it)
            },
            onDone = onSignupClick
        )

        SignupActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, top = 80.dp, end = 50.dp),
            text = stringResource(id = R.string.sign_up).uppercase(),
            onClick = onSignupClick
        )
    }
}

@Composable
private fun SignupFieldLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    val colors = BitcoinTickerColors.current

    Text(
        modifier = modifier,
        text = text,
        color = colors.mutedText,
        fontSize = 12.sp
    )
}

@Composable
private fun SignupTextField(
    value: String,
    hint: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onNext: () -> Unit = {},
    onDone: () -> Unit = {}
) {
    val colors = BitcoinTickerColors.current

    BasicTextField(
        modifier = modifier
            .height(40.dp)
            .background(colors.inputBackground),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = colors.mutedText,
            fontSize = 14.sp
        ),
        cursorBrush = SolidColor(colors.mutedText),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() },
            onDone = { onDone() }
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        color = colors.mutedText,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun SignupActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BitcoinTickerColors.current

    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = colors.action,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.brand,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignupScreenPreview() {
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
