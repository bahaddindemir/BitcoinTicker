package com.bahaddindemir.bitcointicker.ui.splash

import androidx.lifecycle.ViewModel
import com.bahaddindemir.bitcointicker.util.GeneralUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val generalUseCases: GeneralUseCases) : ViewModel()
{
    fun isFirstTime() = generalUseCases.checkFirstTimeUseCase()

    fun isLoggedIn() = generalUseCases.checkLoggedInUserUseCase()

    fun setFirstTime() = generalUseCases.setFirstTimeUseCase(false)
}