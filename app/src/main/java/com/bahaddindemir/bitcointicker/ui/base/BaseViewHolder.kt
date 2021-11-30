package com.bahaddindemir.bitcointicker.ui.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/** BaseViewHolder is an abstract class for structuring the base view holder class. */
@Suppress("LeakingThis")
abstract class BaseViewHolder(view: View) :
    RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    val context: Context = view.context

    init {
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
    }

    /** binds data to the view holder class. */
    @Throws(Exception::class)
    abstract fun bindData(data: Any)
}