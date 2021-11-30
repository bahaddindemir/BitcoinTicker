package com.bahaddindemir.bitcointicker.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.navigation.NavController

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
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
        _binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    open fun setUpBottomNavigation() {}

    open fun setUpViews() {}

    override fun onSupportNavigateUp(): Boolean {
        return navController.value?.navigateUp()!! || super.onSupportNavigateUp()
    }
}