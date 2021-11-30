package com.bahaddindemir.bitcointicker.data.model

import android.util.ArrayMap
import android.util.Log
import retrofit2.Response
import com.google.gson.Gson
import java.io.IOException
import java.util.regex.Pattern

@Suppress("JoinDeclarationAndAssignment")
class ApiResponse<T> {
    val code: Int
    val body: T?
    var links: MutableMap<String, String>
    private val gson: Gson
    val envelope: Envelope?

    val isSuccessful: Boolean
        get() = code in 200..300

    val nextPage: Int?
        get() {
            val next = links[NEXT_LINK] ?: return null
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                return null
            }
            try {
                return Integer.parseInt(matcher.group(1) ?: "")
            } catch (ex: NumberFormatException) {
                Log.w(this.toString(), ex.message!!)
                return null
            }
        }

    init {
        gson = Gson()
        links = emptyMap<String, String>().toMutableMap()
    }

    constructor(error: Throwable) {
        code = 500
        body = null
        envelope = Envelope(error.message.toString(), "")
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            envelope = null
        } else {
            var message: String? = null
            response.errorBody()?.let {
                try {
                    message = response.errorBody()!!.string()
                } catch (ignored: IOException) {
                    Log.w(this.toString(), "error while parsing response " + ignored.message)
                }
            }

            message?.apply {
                if (isNullOrEmpty() || trim { it <= ' ' }.isEmpty()) {
                    message = response.message()
                }
            }

            envelope = gson.fromJson(message, Envelope::class.java)
            body = null
        }

        val linkHeader = response.headers().get("link")
        linkHeader?.let {
            links = ArrayMap()
            val matcher = LINK_PATTERN.matcher(linkHeader)
            while (matcher.find()) {
                if (matcher.groupCount() == 2) {
                    matcher.group(2)?.let {
                        matcher.group(1)?.apply {
                            links.put(it, this)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private val NEXT_LINK = "next"
    }
}