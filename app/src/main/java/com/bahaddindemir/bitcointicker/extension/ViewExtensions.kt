package com.bahaddindemir.bitcointicker.extension

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.Group
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.isVisible
import androidx.core.view.isGone
import androidx.core.view.isInvisible

fun View.show() {
    if (isVisible) return

    visibility = View.VISIBLE
}

fun View.hide() {
    if (isGone) return

    visibility = View.GONE
}

fun View.invisible() {
    if (isInvisible) return

    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

fun View.showSnackBar(message: String, retryActionName: String? = null, action: (() -> Unit)? = null) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)

    val view: View = snackBar.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params

    action?.let {
        snackBar.setAction(retryActionName) {
            it()
        }
    }

    snackBar.show()
}