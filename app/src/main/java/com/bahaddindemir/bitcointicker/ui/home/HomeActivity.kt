package com.bahaddindemir.bitcointicker.ui.home

import android.content.Intent
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.data.services.BackgroundRefreshService
import com.bahaddindemir.bitcointicker.databinding.ActivityHomeBinding
import com.bahaddindemir.bitcointicker.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    override fun getLayoutId() = R.layout.activity_home

    override fun setUpBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.mainBottomNavigation.setupWithNavController(navController)
    }

    override fun setUpViews() {
        startBackgroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.fragmentContainerView))  ||
                super.onOptionsItemSelected(item)
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