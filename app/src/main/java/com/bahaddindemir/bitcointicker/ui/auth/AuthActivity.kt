package com.bahaddindemir.bitcointicker.ui.auth

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.navigation.fragment.NavHostFragment
import com.bahaddindemir.bitcointicker.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthActivityScreen {
                AuthNavHostFragment(supportFragmentManager)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        return navHostFragment?.navController?.navigateUp() == true || super.onSupportNavigateUp()
    }
}

@Composable
fun AuthActivityScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
    }
}

@Composable
private fun AuthNavHostFragment(
    fragmentManager: FragmentManager,
    modifier: Modifier = Modifier
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
                    if (fragmentManager.findFragmentById(id) == null) {
                        val navHostFragment = NavHostFragment.create(R.navigation.nav_auth)
                        fragmentManager.commitNow {
                            setReorderingAllowed(true)
                            replace(id, navHostFragment)
                            setPrimaryNavigationFragment(navHostFragment)
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthActivityScreenPreview() {
    AuthActivityScreen {
        Box(modifier = Modifier.fillMaxSize())
    }
}
