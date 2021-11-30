package com.bahaddindemir.bitcointicker.ui.home

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.services.BackgroundRefreshService
import com.bahaddindemir.bitcointicker.databinding.ActivityHomeBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    override fun getLayoutId() = R.layout.activity_home

    override fun setUpBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        //val navController: NavController =
        //    Navigation.findNavController(this, R.id.fragmentContainerView)
        //NavigationUI.setupWithNavController(binding.mainBottomNavigation, navController)
    }

    override fun setUpViews() {
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