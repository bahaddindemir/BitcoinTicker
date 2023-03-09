package com.bahaddindemir.bitcointicker.ui.intro.tutorial

import androidx.lifecycle.ViewModel
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor() : ViewModel() {
    val openIntro = SingleLiveEvent<Void>()

    fun onSkipClicked() {
        openIntro.call()
    }
}