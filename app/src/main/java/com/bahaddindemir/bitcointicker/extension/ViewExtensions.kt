package com.bahaddindemir.bitcointicker.extension

import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.google.android.material.snackbar.Snackbar
import android.view.Gravity

import android.widget.FrameLayout

fun View.show() {
    if (visibility == View.VISIBLE) return

    visibility = View.VISIBLE
    if (this is Group) {
        this.requestLayout()
    }
}

fun View.hide() {
    if (visibility == View.GONE) return

    visibility = View.GONE
    if (this is Group) {
        this.requestLayout()
    }
}

fun View.invisible() {
    if (visibility == View.INVISIBLE) return

    visibility = View.INVISIBLE
    if (this is Group) {
        this.requestLayout()
    }
}

@BindingAdapter("app:goneUnless")
fun View.goneUnless(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
    if (this is Group) {
        this.requestLayout()
    }
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