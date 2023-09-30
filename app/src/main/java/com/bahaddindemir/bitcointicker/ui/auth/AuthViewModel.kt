package com.bahaddindemir.bitcointicker.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaddindemir.bitcointicker.data.model.AuthRequest
import com.bahaddindemir.bitcointicker.util.AuthUseCase
import com.bahaddindemir.bitcointicker.data.model.Resource
import com.bahaddindemir.bitcointicker.data.repository.auth.AuthRepository
import com.bahaddindemir.bitcointicker.util.AppPreferences
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase,
                                        private val authRepository: AuthRepository,
                                        private val appPreferences: AppPreferences) : ViewModel()
{
    var request = AuthRequest()
    private val _authResponse = MutableStateFlow<Any>(Resource.Default)
    val authResponse = _authResponse

    val validationException = SingleLiveEvent<Int>()
    val successResponse = SingleLiveEvent<Boolean>()
    val toSignupButton = SingleLiveEvent<Boolean>()

    private val disposable = CompositeDisposable()

    val user by lazy {
        authRepository.currentUser()
    }

    fun onLoginClicked() {
        authUseCase()
        login()
    }

    fun onSignupClicked() {
        authUseCase()
        signup()
    }

    fun onToSignupClicked() {
        toSignupButton.value = true
    }

    private fun login() {
        disposable.add(
            authRepository.login(request)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe({
                              appPreferences.isLoggedIn = true
                              successResponse.value = true
                          }, {
                              successResponse.value = false
                              Log.w(this.toString(), it.message!!)
                          })
        )
    }

    private fun signup() {
        disposable.add(
            authRepository.register(request)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe({
                              appPreferences.isLoggedIn = true
                              successResponse.value = true
                          }, {
                              successResponse.value = false
                              Log.w(this.toString(), it.message!!)
                          })
        )
    }

    private fun authUseCase() {
        authUseCase(request)
            .catch { exception -> validationException.value = exception.message?.toInt() }
            .onEach { result -> _authResponse.value = result }
            .launchIn(viewModelScope)
    }
}