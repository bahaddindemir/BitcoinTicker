package com.bahaddindemir.bitcointicker.ui.base

import androidx.lifecycle.ViewModel
import com.bahaddindemir.bitcointicker.util.SingleLiveEvent

open class BaseViewModel : ViewModel() {
    var dataLoadingEvent: SingleLiveEvent<Int> = SingleLiveEvent()
}