package com.bahaddindemir.bitcointicker.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>(private val bindingInflater: (LayoutInflater) -> VB) :
    AppCompatActivity() {
    private var _binding: VB? = null
    open val binding get() = _binding!!
    lateinit var navController: LiveData<NavController>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setContentView(binding.root)

        if (savedInstanceState == null) {
            setUpBottomNavigation()
        }

        setUpViews()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        setUpBottomNavigation()
    }

    private fun initViewBinding() {
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
    }

    open fun setUpBottomNavigation() {}

    open fun setUpViews() {}

    override fun onSupportNavigateUp(): Boolean {
        return navController.value?.navigateUp()!! || super.onSupportNavigateUp()
    }
}