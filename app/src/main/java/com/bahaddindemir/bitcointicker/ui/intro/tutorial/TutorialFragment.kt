package com.bahaddindemir.bitcointicker.ui.intro.tutorial

import androidx.fragment.app.viewModels
import com.bahaddindemir.bitcointicker.databinding.FragmentTutorialBinding
import com.bahaddindemir.bitcointicker.extension.navigateSafe
import com.bahaddindemir.bitcointicker.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialFragment : BaseFragment<FragmentTutorialBinding>(FragmentTutorialBinding::inflate) {
    private val viewModel: TutorialViewModel by viewModels()

    override
    fun setUpViews() {
        //setUpAppTutorial()
    }

    override
    fun setupObservers() {
        binding.tvSkip.setOnClickListener { openIntro() }
    }

    private fun openIntro() {
        navigateSafe(TutorialFragmentDirections.actionTutorialFragmentToIntroFragment())
    }
}