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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.model.AuthFieldsValidation
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.ui.components.LoadingDialog
import com.bahaddindemir.bitcointicker.ui.main.MainActivity
import com.bahaddindemir.bitcointicker.ui.splash.SplashScreen
import com.bahaddindemir.bitcointicker.ui.splash.SplashUiEvent
import com.bahaddindemir.bitcointicker.ui.splash.SplashViewModel
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerColors
import com.bahaddindemir.bitcointicker.ui.theme.BitcoinTickerTheme
import dagger.hilt.android.AndroidEntryPoint

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
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SplashUiEvent.OpenLogin -> openLogin()
                SplashUiEvent.OpenHome -> openHome()
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.start()
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val someErrorMessage = stringResource(id = R.string.some_error)
    val emptyEmailMessage = stringResource(id = R.string.empty_email)
    val invalidEmailMessage = stringResource(id = R.string.invalid_email)
    val emptyPasswordMessage = stringResource(id = R.string.empty_password)

    LaunchedEffect(viewModel, route) {
        viewModel.events.collect { event ->
            when (event) {
                AuthUiEvent.AuthSucceeded -> openHome()
                AuthUiEvent.AuthFailed -> snackbarHostState.showSnackbar(someErrorMessage)
                is AuthUiEvent.ValidationFailed -> {
                    val message = when (event.validationType) {
                        AuthFieldsValidation.EMPTY_EMAIL.value -> emptyEmailMessage
                        AuthFieldsValidation.INVALID_EMAIL.value -> invalidEmailMessage
                        AuthFieldsValidation.EMPTY_PASSWORD.value -> emptyPasswordMessage
                        else -> someErrorMessage
                    }
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            keyboardController?.hide()
        }
    }

    when (route) {
        AuthRoute.Login -> LoginScreen(
            initialEmail = uiState.email,
            initialPassword = uiState.password,
            focusTarget = null,
            onFocusHandled = {},
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = { viewModel.onLoginClicked() },
            onSignupClick = openSignup
        )

        AuthRoute.Signup -> SignupScreen(
            initialEmail = uiState.email,
            initialPassword = uiState.password,
            focusTarget = null,
            onFocusHandled = {},
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onSignupClick = { viewModel.onSignupClicked() }
        )

        AuthRoute.Splash -> Unit
    }

    LoadingDialog(isVisible = uiState.isLoading)
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
