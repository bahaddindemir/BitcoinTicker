package com.bahaddindemir.bitcointicker.ui.intro.intro

import com.bahaddindemir.bitcointicker.ui.base.BaseViewModel
import com.bahaddindemir.bitcointicker.util.GeneralUseCases
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(private val generalUseCases: GeneralUseCases) :
    BaseViewModel() {

    val openLogIn = SingleLiveEvent<Void>()

    fun onLogInClicked() {
        openLogIn.call()
    }

    fun setFirstTime(isFirstTime: Boolean) = generalUseCases.setFirstTimeUseCase(isFirstTime)
}