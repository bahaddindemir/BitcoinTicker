package com.bahaddindemir.bitcointicker.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.services.BackgroundRefreshService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
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

    Column(modifier = modifier.fillMaxSize()) {
        MainNavHostFragment(
            fragmentManager = fragmentManager,
            modifier = Modifier.weight(1f),
            onNavControllerReady = { controller ->
                navController = controller
                onNavControllerReady(controller)
                controller.addOnDestinationChangedListener { _, destination, _ ->
                    isBottomNavigationVisible = destination.id != R.id.detail_fragment
                }
            }
        )

        val controller = navController
        if (controller != null && isBottomNavigationVisible) {
            MainBottomNavigation(
                navController = controller,
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
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BottomNavigationView(context).apply {
                id = R.id.main_bottom_navigation
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setBackgroundColor(ContextCompat.getColor(context, R.color.splash))
                itemIconSize = (40 * resources.displayMetrics.density).toInt()
                itemIconTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.button_background)
                )
                itemTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
                inflateMenu(R.menu.bottom_menu)
                setupWithNavController(navController)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MainActivityScreenPreview() {
    Column(modifier = Modifier.fillMaxSize()) {}
}
