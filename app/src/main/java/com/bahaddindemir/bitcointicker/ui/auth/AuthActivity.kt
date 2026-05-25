package com.bahaddindemir.bitcointicker.ui.auth

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.ui.components.LoadingDialog
import com.bahaddindemir.bitcointicker.ui.main.MainActivity
import com.bahaddindemir.bitcointicker.ui.splash.SplashScreen
import com.bahaddindemir.bitcointicker.ui.splash.SplashViewModel
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerColors
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitcoinTickerTheme {
                AuthActivityScreen(
                    openHome = { openActivityAndClearStack(MainActivity::class.java) }
                )
            }
        }
    }
}

@Composable
fun AuthActivityScreen(
    openHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = BitcoinTickerColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    ) { paddingValues ->
        AuthNavHost(
            navController = navController,
            snackbarHostState = snackbarHostState,
            openHome = openHome,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun AuthNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    openHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AuthRoute.Splash.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(AuthRoute.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashRoute(
                viewModel = viewModel,
                openLogin = {
                    navController.navigate(AuthRoute.Login.route) {
                        popUpTo(AuthRoute.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                openHome = openHome
            )
        }
        composable(AuthRoute.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            AuthRouteContent(
                route = AuthRoute.Login,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                openHome = openHome,
                openSignup = { navController.navigate(AuthRoute.Signup.route) }
            )
        }
        composable(AuthRoute.Signup.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            AuthRouteContent(
                route = AuthRoute.Signup,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                openHome = openHome,
                openSignup = {}
            )
        }
    }
}

@Composable
private fun SplashRoute(
    viewModel: SplashViewModel,
    openLogin: () -> Unit,
    openHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MILLIS)
        if (viewModel.isFirstTime()) {
            viewModel.setFirstTime()
            openLogin()
        } else if (viewModel.isLoggedIn()) {
            openHome()
        } else {
            openLogin()
        }
    }

    SplashScreen()
}

@Composable
private fun AuthRouteContent(
    route: AuthRoute,
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
    openHome: () -> Unit,
    openSignup: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val someErrorMessage = stringResource(id = R.string.some_error)
    val emptyEmailMessage = stringResource(id = R.string.empty_email)
    val invalidEmailMessage = stringResource(id = R.string.invalid_email)
    val emptyPasswordMessage = stringResource(id = R.string.empty_password)

    LaunchedEffect(viewModel, route) {
        launch {
            viewModel.successResponse.collect { isSuccess ->
                if (isSuccess) {
                    openHome()
                } else {
                    snackbarHostState.showSnackbar(someErrorMessage)
                }
            }
        }

        launch {
            viewModel.validationException.collect { validationType ->
                val message = when (validationType) {
                    AuthFieldsValidation.EMPTY_EMAIL.value -> emptyEmailMessage
                    AuthFieldsValidation.INVALID_EMAIL.value -> invalidEmailMessage
                    AuthFieldsValidation.EMPTY_PASSWORD.value -> emptyPasswordMessage
                    else -> someErrorMessage
                }
                snackbarHostState.showSnackbar(message)
            }
        }

        launch {
            viewModel.isLoading.collect { loading ->
                isLoading = loading
                if (loading) {
                    keyboardController?.hide()
                }
            }
        }
    }

    when (route) {
        AuthRoute.Login -> LoginScreen(
            initialEmail = viewModel.request.email,
            initialPassword = viewModel.request.password,
            focusTarget = null,
            onFocusHandled = {},
            onEmailChange = { viewModel.request.email = it },
            onPasswordChange = { viewModel.request.password = it },
            onLoginClick = { viewModel.onLoginClicked() },
            onSignupClick = openSignup
        )

        AuthRoute.Signup -> SignupScreen(
            initialEmail = viewModel.request.email,
            initialPassword = viewModel.request.password,
            focusTarget = null,
            onFocusHandled = {},
            onEmailChange = { viewModel.request.email = it },
            onPasswordChange = { viewModel.request.password = it },
            onSignupClick = { viewModel.onSignupClicked() }
        )

        AuthRoute.Splash -> Unit
    }

    LoadingDialog(isVisible = isLoading)
}

@Preview(showBackground = true)
@Composable
private fun AuthActivityScreenPreview() {
    Box(modifier = Modifier.fillMaxSize())
}

private enum class AuthRoute(val route: String) {
    Splash("splash"),
    Login("login"),
    Signup("signup")
}

private const val SPLASH_DELAY_MILLIS = 2000L
