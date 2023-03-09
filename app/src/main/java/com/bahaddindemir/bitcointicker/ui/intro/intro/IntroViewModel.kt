package com.bahaddindemir.bitcointicker.ui.intro.intro

import androidx.lifecycle.ViewModel
import com.bahaddindemir.bitcointicker.util.GeneralUseCases
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(private val generalUseCases: GeneralUseCases) : ViewModel() {

    val openLogIn = SingleLiveEvent<Void>()

    fun onLogInClicked() {
        openLogIn.call()
    }

    fun setFirstTime(isFirstTime: Boolean) = generalUseCases.setFirstTimeUseCase(isFirstTime)
}