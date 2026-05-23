package com.bahaddindemir.bitcointicker.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private val viewModel: SplashViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val navigateRunnable = Runnable {
        if (isAdded) {
            //ToDo: Add intro screen if you have time
            if (viewModel.isFirstTime()) {
                viewModel.setFirstTime()
                navigateSafe(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                //navigateSafe(SplashFragmentDirections.actionSplashFragmentToTutorialFragment())
            } else if (viewModel.isLoggedIn()) {
                openActivityAndClearStack(MainActivity::class.java)
            } else {
                navigateSafe(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SplashScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postDelayed(navigateRunnable, SPLASH_DELAY_MILLIS)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(navigateRunnable)
        super.onDestroyView()
    }

    private companion object {
        const val SPLASH_DELAY_MILLIS = 2000L
    }
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val splashColor = colorResource(id = R.color.splash)
    val splashAccentColor = colorResource(id = R.color.splash_accent)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(splashColor, splashAccentColor)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(bottom = 50.dp),
            text = stringResource(id = R.string.splash_text),
            color = Color.White,
            fontSize = 100.sp
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 50.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}