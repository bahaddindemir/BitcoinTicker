package com.bahaddindemir.bitcointicker.data.repository.network

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException
import java.util.regex.Pattern

abstract class NetworkBoundRepository<ResultType, RequestType>
internal constructor() {

    fun asFlow(): Flow<CoinResource<ResultType>> = flow {
        Log.v(this@NetworkBoundRepository.toString(), "Injection NetworkBoundRepository")

        val cachedData = loadFromDb().firstOrNull()

        if (!shouldFetch(cachedData)) {
            emitDbSuccess(nextPage = 1)
            return@flow
        }

        emit(CoinResource.Loading)

        val response = try {
            fetchService()
        } catch (throwable: Throwable) {
            emitFetchError(throwable.message.orEmpty())
            return@flow
        }

        if (response.isSuccessful) {
            response.body()?.let { saveFetchData(it) }
            emitDbSuccess(nextPage = response.nextPage())
        } else {
            emitFetchError(response.errorMessage())
        }
    }

    private suspend fun FlowCollector<CoinResource<ResultType>>.emitDbSuccess(nextPage: Int?) {
        loadFromDb()
            .map { CoinResource.Success(it, nextPage) }
            .collect { emit(it) }
    }

    private suspend fun FlowCollector<CoinResource<ResultType>>.emitFetchError(message: String) {
        onFetchFailed(message)

        loadFromDb()
            .map { CoinResource.Error(message, it) }
            .collect { emit(it) }
    }

    @WorkerThread
    protected abstract fun saveFetchData(items: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flow<ResultType?>

    @WorkerThread
    protected abstract suspend fun fetchService(): Response<RequestType>

    @MainThread
    protected abstract fun onFetchFailed(message: String)
}

private val LINK_PATTERN: Pattern = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
private val PAGE_PATTERN: Pattern = Pattern.compile("\\bpage=(\\d+)")
private const val NEXT_LINK = "next"

private fun Response<*>.nextPage(): Int? {
    val linkHeader = headers()["link"] ?: return null
    val matcher = LINK_PATTERN.matcher(linkHeader)
    while (matcher.find()) {
        if (matcher.groupCount() == 2 && matcher.group(2) == NEXT_LINK) {
            return matcher.group(1)
                ?.let { PAGE_PATTERN.matcher(it) }
                ?.takeIf { it.find() && it.groupCount() == 1 }
                ?.group(1)
                ?.toIntOrNull()
        }
    }
    return null
}

private fun Response<*>.errorMessage(): String {
    val fallbackMessage = message().takeIf { it.isNotBlank() }.orEmpty()
    val errorBodyMessage = try {
        errorBody()?.string()
    } catch (_: IOException) {
        null
    }
    return errorBodyMessage
        ?.takeIf { it.isNotBlank() }
        ?: fallbackMessage
}
