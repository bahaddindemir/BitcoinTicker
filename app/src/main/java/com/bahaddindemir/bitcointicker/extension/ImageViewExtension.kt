package com.bahaddindemir.bitcointicker.extension

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bahaddindemir.bitcointicker.R
import com.bumptech.glide.Glide

fun ImageView.loadImage(
    url: String?,
    errorDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_fg)
) {
    Glide.with(this)
        .load(url)
        .error(errorDrawable)
        .into(this)
}