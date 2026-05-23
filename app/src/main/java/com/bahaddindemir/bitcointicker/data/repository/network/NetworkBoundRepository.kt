package com.bahaddindemir.bitcointicker.data.repository.network

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import com.bahaddindemir.bitcointicker.data.model.Envelope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

abstract class NetworkBoundRepository<ResultType, RequestType>
internal constructor() {

    fun asFlow(): Flow<CoinResource<ResultType>> = flow {
        Log.v(this@NetworkBoundRepository.toString(), "Injection NetworkBoundRepository")
        val loadedFromDb = loadFromDb()
        val cachedData = loadedFromDb.first()

        if (shouldFetch(cachedData)) {
            emit(CoinResource.Loading)
            val response = try {
                ApiResponse(fetchService())
            } catch (throwable: Throwable) {
                ApiResponse(throwable)
            }

            if (response.isSuccessful) {
                response.body?.let { saveFetchData(it) }
                loadFromDb()
                    .map { CoinResource.Success(it, response.nextPage) }
                    .collect { emit(it) }
            } else {
                onFetchFailed(response.envelope)
                loadFromDb()
                    .map { CoinResource.Error(response.envelope?.message.orEmpty(), it) }
                    .collect { emit(it) }
            }
        } else {
            loadedFromDb
                .map { CoinResource.Success(it, 1) }
                .collect { emit(it) }
        }
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
    protected abstract fun onFetchFailed(envelope: Envelope?)
}
