package com.bahaddindemir.bitcointicker.ui.intro.intro

import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.FragmentIntroBinding
import com.bahaddindemir.bitcointicker.extension.openActivityAndClearStack
import com.bahaddindemir.bitcointicker.ui.auth.AuthActivity
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : BaseFragment<FragmentIntroBinding>() {
    private val viewModel: IntroViewModel by viewModels()

    override
    fun getLayoutId() = R.layout.fragment_intro

    override
    fun setBindingVariables() {
        binding.viewModel = viewModel
    }

    override
    fun setupObservers() {
        viewModel.openLogIn.observe(this) {
            viewModel.setFirstTime(false)
            openLogIn()
        }
    }

    private fun openLogIn() {
        openActivityAndClearStack(AuthActivity::class.java)
    }
}