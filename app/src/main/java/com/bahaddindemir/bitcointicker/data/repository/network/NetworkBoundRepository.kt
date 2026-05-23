package com.bahaddindemir.bitcointicker.data.repository.network

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import com.bahaddindemir.bitcointicker.data.model.Envelope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@Suppress("LeakingThis")
abstract class NetworkBoundRepository<ResultType, RequestType>
internal constructor() {
    private val result: MediatorLiveData<CoinResource<ResultType>> = MediatorLiveData()
    private val repositoryScope = CoroutineScope(Dispatchers.Main)

    init {
        Log.v(this.toString(),"Injection NetworkBoundRepository")
        val loadedFromDb = loadFromDb()

        result.addSource(loadedFromDb) { data ->
            result.removeSource(loadedFromDb)
            if (shouldFetch(data)) {
                result.postValue(CoinResource.loading(null, null))
                fetchFromNetwork(loadFromDb())
            } else {
                result.addSource(loadedFromDb) { newData ->
                    setValue(CoinResource.success(newData, 1))
                }
            }
        }
    }

    private fun fetchFromNetwork(loadedFromDB: LiveData<ResultType>) {
        repositoryScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    ApiResponse(fetchService())
                } catch (throwable: Throwable) {
                    ApiResponse(throwable)
                }
            }

            when (response.isSuccessful) {
                true -> {
                    response.body?.let {
                        withContext(Dispatchers.IO) {
                            saveFetchData(it)
                        }
                        val loaded = loadFromDb()

                        result.addSource(loaded) { newData ->
                            newData?.let {
                                setValue(CoinResource.success(newData, response.nextPage))
                            }
                        }
                    }
                }

                false -> {
                    result.removeSource(loadedFromDB)
                    onFetchFailed(response.envelope)
                    response.envelope?.let {
                        result.addSource(loadedFromDB) { newData ->
                            setValue(CoinResource.error(it.message, newData))
                        }
                    }
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: CoinResource<ResultType>) {
        result.value = newValue
    }

    fun asLiveData(): LiveData<CoinResource<ResultType>> {
        return result
    }

    @WorkerThread
    protected abstract fun saveFetchData(items: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @WorkerThread
    protected abstract suspend fun fetchService(): Response<RequestType>

    @MainThread
    protected abstract fun onFetchFailed(envelope: Envelope?)
}
