package com.bahaddindemir.bitcointicker.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.data.model.Resource
import com.bahaddindemir.bitcointicker.extension.hideKeyboard
import com.bahaddindemir.bitcointicker.extension.hideLoadingDialog
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.extension.showError
import com.bahaddindemir.bitcointicker.extension.showLoadingDialog
import com.bahaddindemir.bitcointicker.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()

    private var focusTarget by mutableStateOf<LoginFocusTarget?>(null)
    private var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoginScreen(
                    initialEmail = viewModel.request.email,
                    initialPassword = viewModel.request.password,
                    focusTarget = focusTarget,
                    onFocusHandled = { focusTarget = null },
                    onEmailChange = { viewModel.request.email = it },
                    onPasswordChange = { viewModel.request.password = it },
                    onLoginClick = { viewModel.onLoginClicked() },
                    onSignupClick = ::openSignUp
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.successResponse.observe(viewLifecycleOwner) {
            if (it) {
                hideLoading()
                openHome()
            } else {
                hideLoading()
                showError(resources.getString(R.string.some_error))
            }
        }

        viewModel.validationException.observe(viewLifecycleOwner) {
            when (it) {
                AuthFieldsValidation.EMPTY_EMAIL.value -> {
                    focusTarget = LoginFocusTarget.Email
                    showError(resources.getString(R.string.empty_email))
                }

                AuthFieldsValidation.INVALID_EMAIL.value -> {
                    focusTarget = LoginFocusTarget.Email
                    showError(resources.getString(R.string.invalid_email))
                }

                AuthFieldsValidation.EMPTY_PASSWORD.value -> {
                    focusTarget = LoginFocusTarget.Password
                    showError(resources.getString(R.string.empty_password))
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.authResponse.collect {
                    when (it) {
                        Resource.Loading -> {
                            hideKeyboard()
                            showLoading()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        hideLoading()
        super.onDestroyView()
    }

    private fun openSignUp() {
        navigateSafe(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    private fun openHome() {
        requireActivity().openActivityAndClearStack(MainActivity::class.java)
    }

    private fun showLoading() {
        hideLoading()
        progressDialog = showLoadingDialog()
    }

    private fun hideLoading() {
        progressDialog.hideLoadingDialog(requireActivity())
        progressDialog = null
    }
}

enum class LoginFocusTarget {
    Email,
    Password
}

const val LOGIN_EMAIL_FIELD_TAG = "login_email_field"
const val LOGIN_PASSWORD_FIELD_TAG = "login_password_field"

@Composable
fun LoginScreen(
    initialEmail: String,
    initialPassword: String,
    focusTarget: LoginFocusTarget?,
    onFocusHandled: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val splashColor = colorResource(id = R.color.splash)
    val splashAccentColor = colorResource(id = R.color.splash_accent)

    LaunchedEffect(focusTarget) {
        when (focusTarget) {
            LoginFocusTarget.Email -> emailFocusRequester.requestFocus()
            LoginFocusTarget.Password -> passwordFocusRequester.requestFocus()
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
                    colors = listOf(splashColor, splashAccentColor)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 50.dp),
            text = stringResource(id = R.string.welcome).uppercase(),
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        LoginFieldLabel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 50.dp),
            text = stringResource(id = R.string.email).uppercase()
        )
        LoginTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                .testTag(LOGIN_EMAIL_FIELD_TAG)
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

        LoginFieldLabel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 50.dp),
            text = stringResource(id = R.string.password).uppercase()
        )
        LoginTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                .testTag(LOGIN_PASSWORD_FIELD_TAG)
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
            onDone = onLoginClick
        )

        LoginActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, top = 80.dp, end = 50.dp),
            text = stringResource(id = R.string.log_in).uppercase(),
            onClick = onLoginClick
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 50.dp, end = 20.dp)
                .height(40.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSignupClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.log_in_to_sign_up).uppercase(),
                color = colorResource(id = R.color.gray),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoginFieldLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        color = colorResource(id = R.color.gray),
        fontSize = 12.sp
    )
}

@Composable
private fun LoginTextField(
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
    val gray = colorResource(id = R.color.gray)

    BasicTextField(
        modifier = modifier
            .height(40.dp)
            .background(colorResource(id = R.color.input)),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            color = gray,
            fontSize = 14.sp
        ),
        cursorBrush = SolidColor(gray),
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
                        color = gray,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun LoginActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = colorResource(id = R.color.button_background),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colorResource(id = R.color.splash),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
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
