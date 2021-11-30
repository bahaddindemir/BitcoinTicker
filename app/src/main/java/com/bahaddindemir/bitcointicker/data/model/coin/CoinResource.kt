package com.bahaddindemir.bitcointicker.data.model.coin

import com.bahaddindemir.bitcointicker.data.model.FetchStatus
import com.bahaddindemir.bitcointicker.data.model.Status

class CoinResource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val fetchStatus: FetchStatus
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val resource = other as CoinResource<*>?

        if (status !== resource!!.status) {
            return false
        }

        if (if (message != null) message != resource!!.message else resource!!.message != null) {
            return false
        }
        return if (data != null) data == resource.data else resource.data == null
    }

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + status.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + fetchStatus.hashCode()
        return result
    }

    companion object {
        private var fetchStatus = FetchStatus()

        fun <T> success(data: T?, nextPage: Int?): CoinResource<T> {
            fetchStatus = FetchStatus(false, true, false, false, nextPage)
            if (nextPage == null) fetchStatus = FetchStatus(false, true, false, true, nextPage)
            return CoinResource(Status.SUCCESS, data, null, fetchStatus)
        }

        fun <T> error(msg: String, data: T?): CoinResource<T> {
            fetchStatus = FetchStatus(false, false, true, true)
            return CoinResource(Status.ERROR, data, msg, fetchStatus)
        }

        fun <T> loading(data: T?, nextPage: Int?): CoinResource<T> {
            fetchStatus = FetchStatus(true, false, false, false, nextPage)
            return CoinResource(Status.LOADING, data, null, fetchStatus)
        }
    }
}