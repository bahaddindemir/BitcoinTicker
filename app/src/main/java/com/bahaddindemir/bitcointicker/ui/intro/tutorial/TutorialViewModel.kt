package com.bahaddindemir.bitcointicker.ui.intro.tutorial

import com.bahaddindemir.bitcointicker.ui.base.BaseViewModel
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor() : BaseViewModel() {
    val openIntro = SingleLiveEvent<Void>()

    fun onSkipClicked() {
        openIntro.call()
    }
}