package com.bahaddindemir.bitcointicker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.services.BackgroundRefreshService
import com.bahaddindemir.bitcointicker.ui.detail.DetailRoute
import com.bahaddindemir.bitcointicker.ui.home.HomeRoute
import com.bahaddindemir.bitcointicker.ui.mycoin.MyCoinRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityScreen()
        }

        startBackgroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    private fun startBackgroundService() {
        val serviceIntent = Intent(this, BackgroundRefreshService::class.java)
        serviceIntent.putExtra("isStart", true)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, BackgroundRefreshService::class.java)
        serviceIntent.putExtra("isStart", false)
        stopService(serviceIntent)
    }
}

@Composable
fun MainActivityScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomNavigationVisible = currentRoute != MainRoute.Detail.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorResource(id = R.color.splash_accent),
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomNavigationVisible,
                enter = slideInVertically(
                    animationSpec = tween(MAIN_BOTTOM_BAR_ANIMATION_DURATION_MS),
                    initialOffsetY = { height -> height }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(MAIN_BOTTOM_BAR_ANIMATION_DURATION_MS),
                    targetOffsetY = { height -> height }
                )
            ) {
                MainBottomNavigation(
                    navController = navController,
                    currentRoute = currentRoute,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        MainNavHost(
            navController = navController,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun MainNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.Home.route,
        modifier = modifier.fillMaxSize(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(MainRoute.Home.route) {
            HomeRoute(
                snackbarHostState = snackbarHostState,
                onCoinClick = { coin ->
                    navController.navigate(MainRoute.Detail.createRoute(coin.id))
                }
            )
        }
        composable(MainRoute.MyCoins.route) {
            MyCoinRoute(
                onCoinClick = { coin ->
                    navController.navigate(MainRoute.Detail.createRoute(coin.id))
                }
            )
        }
        composable(
            route = MainRoute.Detail.route,
            arguments = listOf(navArgument(DETAIL_COIN_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString(DETAIL_COIN_ID_ARG).orEmpty()
            DetailRoute(
                coinId = coinId,
                snackbarHostState = snackbarHostState,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.navigateToBottomDestination(route: MainRoute) {
    navigate(route.route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private enum class MainRoute(val route: String) {
    Home("home"),
    MyCoins("my_coins"),
    Detail("detail/{$DETAIL_COIN_ID_ARG}");

    fun createRoute(coinId: String): String {
        return when (this) {
            Detail -> "detail/$coinId"
            else -> route
        }
    }

    companion object {
        fun fromRoute(route: String?): MainRoute? {
            return entries.firstOrNull { item ->
                route == item.route
            }
        }
    }
}

@Composable
private fun MainBottomNavigation(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = colorResource(id = R.color.splash)
    ) {
        MainBottomNavigationItem.entries.forEach { item ->
            NavigationBarItem(
                selected = MainRoute.fromRoute(currentRoute) == item.route,
                onClick = {
                    if (MainRoute.fromRoute(currentRoute) != item.route) {
                        navController.navigateToBottomDestination(item.route)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = stringResource(id = item.labelResId),
                        modifier = Modifier.size(40.dp)
                    )
                },
                label = {
                    Text(text = stringResource(id = item.labelResId))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(id = R.color.button_background),
                    unselectedIconColor = colorResource(id = R.color.button_background),
                    selectedTextColor = colorResource(id = R.color.white),
                    unselectedTextColor = colorResource(id = R.color.white),
                    indicatorColor = colorResource(id = R.color.splash)
                )
            )
        }
    }
}

private enum class MainBottomNavigationItem(
    val route: MainRoute,
    val iconResId: Int,
    val labelResId: Int,
) {
    Home(
        route = MainRoute.Home,
        iconResId = R.drawable.ic_main,
        labelResId = R.string.home
    ),
    MyCoins(
        route = MainRoute.MyCoins,
        iconResId = R.drawable.ic_my_coins,
        labelResId = R.string.my_coins_fragment
    )
}

@Preview(showBackground = true)
@Composable
private fun MainActivityScreenPreview() {
    Box(modifier = Modifier.fillMaxSize())
}

private const val DETAIL_COIN_ID_ARG = "coinId"
private const val MAIN_BOTTOM_BAR_ANIMATION_DURATION_MS = 180
