package com.bahaddindemir.bitcointicker.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.services.BackgroundRefreshService
import com.bahaddindemir.bitcointicker.extension.navigateToBottomDestination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityScreen(supportFragmentManager) {
                navController = it
            }
        }

        startBackgroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return navController?.let { item.onNavDestinationSelected(it) } == true ||
                super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() == true || super.onSupportNavigateUp()
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
    fragmentManager: FragmentManager,
    modifier: Modifier = Modifier,
    onNavControllerReady: (NavController) -> Unit
) {
    var navController by remember { mutableStateOf<NavController?>(null) }
    var isBottomNavigationVisible by remember { mutableStateOf(true) }
    var selectedDestinationId by remember { mutableIntStateOf(R.id.home_fragment) }

    Column(modifier = modifier.fillMaxSize()) {
        MainNavHostFragment(
            fragmentManager = fragmentManager,
            modifier = Modifier.weight(1f),
            onNavControllerReady = { controller ->
                navController = controller
                onNavControllerReady(controller)
                controller.addOnDestinationChangedListener { _, destination, _ ->
                    isBottomNavigationVisible = destination.id != R.id.detail_fragment
                    selectedDestinationId = destination.id
                }
            }
        )

        val controller = navController
        if (controller != null && isBottomNavigationVisible) {
            MainBottomNavigation(
                navController = controller,
                selectedDestinationId = selectedDestinationId,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MainNavHostFragment(
    fragmentManager: FragmentManager,
    modifier: Modifier = Modifier,
    onNavControllerReady: (NavController) -> Unit
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            FragmentContainerView(context).apply {
                id = R.id.fragmentContainerView
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                doOnAttach {
                    val navHostFragment = fragmentManager.findFragmentById(id) as? NavHostFragment
                        ?: NavHostFragment.create(R.navigation.nav_home).also { navHost ->
                            fragmentManager.commitNow {
                                setReorderingAllowed(true)
                                replace(id, navHost)
                                setPrimaryNavigationFragment(navHost)
                            }
                        }
                    onNavControllerReady(navHostFragment.navController)
                }
            }
        }
    )
}

@Composable
private fun MainBottomNavigation(
    navController: NavController,
    selectedDestinationId: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = colorResource(id = R.color.splash)
    ) {
        MainBottomNavigationItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedDestinationId == item.destinationId,
                onClick = {
                    if (selectedDestinationId != item.destinationId) {
                        navController.navigateToBottomDestination(item.destinationId)
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
    val destinationId: Int,
    val iconResId: Int,
    val labelResId: Int,
) {
    Home(
        destinationId = R.id.home_fragment,
        iconResId = R.drawable.ic_main,
        labelResId = R.string.home
    ),
    MyCoins(
        destinationId = R.id.my_coin_fragment,
        iconResId = R.drawable.ic_my_coins,
        labelResId = R.string.my_coins_fragment
    )
}

@Preview(showBackground = true)
@Composable
private fun MainActivityScreenPreview() {
    Column(modifier = Modifier.fillMaxSize()) {}
}
