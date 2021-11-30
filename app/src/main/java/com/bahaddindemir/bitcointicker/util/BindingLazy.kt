package com.bahaddindemir.bitcointicker.util

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

inline fun <reified T : ViewDataBinding> bindings(view: View): Lazy<T> =
    lazy {
        DataBindingUtil.bind<T>(view)?.let { it }
            ?: throw IllegalAccessException("cannot find the matched view to layout.")
    }