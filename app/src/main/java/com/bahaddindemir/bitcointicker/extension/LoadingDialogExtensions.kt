package com.bahaddindemir.bitcointicker.extension

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.bahaddindemir.bitcointicker.R

fun Fragment.showLoadingDialog(hint: String? = null): Dialog? {
    val activity = activity
    if (activity == null || activity.isFinishing) {
        return null
    }

    return Dialog(activity).apply {
        show()
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        setContentView(R.layout.progress_dialog)

        findViewById<TextView>(R.id.tv_hint).apply {
            if (!hint.isNullOrEmpty()) {
                show()
                text = hint
            } else {
                hide()
            }
        }

        setCancelable(false)
        setCanceledOnTouchOutside(false)
        show()
    }
}

fun Dialog?.hideLoadingDialog(activity: Activity?) {
    if (activity != null && !activity.isFinishing && this != null && isShowing) {
        dismiss()
    }
}
