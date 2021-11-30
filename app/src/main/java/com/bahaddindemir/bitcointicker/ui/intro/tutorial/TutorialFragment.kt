package com.bahaddindemir.bitcointicker.ui.intro.tutorial

import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.R
import com.bahaddindemir.bitcointicker.databinding.FragmentTutorialBinding
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialFragment : BaseFragment<FragmentTutorialBinding>() {
    private val viewModel: TutorialViewModel by viewModels()

    override
    fun getLayoutId() = R.layout.fragment_tutorial

    override
    fun setBindingVariables() {
        binding.viewModel = viewModel
    }

    override
    fun setUpViews() {
        //setUpAppTutorial()
    }

    override
    fun setupObservers() {
        viewModel.openIntro.observe(this) { openIntro() }
    }

    private fun openIntro() {
        //navigateSafe(TutorialFragmentDirections.actionOpenIntroFragment())
    }
}