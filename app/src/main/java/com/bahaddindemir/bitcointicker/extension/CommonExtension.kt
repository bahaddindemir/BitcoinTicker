package com.bahaddindemir.bitcointicker.extension

import com.google.gson.Gson

fun Any.toJsonString(): String = Gson().toJson(this)

fun <A : Any> String.toJsonModel(modelClass: Class<A>): A = Gson().fromJson(this, modelClass)