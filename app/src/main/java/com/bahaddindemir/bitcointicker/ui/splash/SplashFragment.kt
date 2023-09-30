package com.bahaddindemir.bitcointicker.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.FragmentSplashBinding
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import com.bahaddindemir.bitcointicker.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val viewModel: SplashViewModel by viewModels()

    override fun getLayoutId() = R.layout.fragment_splash

    override fun setUpViews() {
        Handler(Looper.getMainLooper()).postDelayed({
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
        }, 2000)
    }
}