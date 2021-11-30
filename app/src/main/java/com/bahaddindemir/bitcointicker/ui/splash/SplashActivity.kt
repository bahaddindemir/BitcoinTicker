package com.bahaddindemir.bitcointicker.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.ActivitySplashBinding
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.ui.auth.AuthActivity
import com.bahaddindemir.bitcointicker.ui.base.BaseActivity
import com.bahaddindemir.bitcointicker.ui.home.HomeActivity
import com.bahaddindemir.bitcointicker.ui.intro.IntroActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel: SplashViewModel by viewModels()

    override fun getLayoutId() = R.layout.activity_splash

    override fun setUpViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            //ToDo: Add intro screen if you have time
            val targetActivity = if (viewModel.isFirstTime()) {
                //IntroActivity::class.java
                AuthActivity::class.java
            } else if (viewModel.isLoggedIn()){
                HomeActivity::class.java
            } else {
                AuthActivity::class.java
            }
            openActivityAndClearStack(targetActivity)
        }, 2000)

        if (viewModel.isFirstTime()) viewModel.setFirstTime()
    }
}