package com.bahaddindemir.bitcointicker.data.repository.network

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import com.bahaddindemir.bitcointicker.data.model.Envelope
import com.bahaddindemir.bitcointicker.data.model.coin.CoinResource

@Suppress("LeakingThis")
abstract class NetworkBoundRepository<ResultType, RequestType>
internal constructor() {
    private val result: MediatorLiveData<CoinResource<ResultType>> = MediatorLiveData()

    init {
        Log.v(this.toString(),"Injection NetworkBoundRepository")
        val loadedFromDb = loadFromDb()

        result.addSource(loadedFromDb) { data ->
            result.removeSource(loadedFromDb)
            if (shouldFetch(data)) {
                result.postValue(CoinResource.loading(null, null))
                fetchFromNetwork(loadFromDb())
            } else {
                result.addSource<ResultType>(loadedFromDb) { newData ->
                    setValue(
                        CoinResource.success(
                            newData,
                            1
                        )
                    )
                }
            }
        }
    }

    private fun fetchFromNetwork(loadedFromDB: LiveData<ResultType>) {
        val apiResponse = fetchService()

        result.addSource(apiResponse) { response ->
            response?.let {
                when (response.isSuccessful) {
                    true -> {
                        response.body?.let {
                            saveFetchData(it)
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
                            result.addSource<ResultType>(loadedFromDB) { newData ->
                                setValue(
                                    CoinResource.error(it.message, newData)
                                )
                            }
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

    @MainThread
    protected abstract fun fetchService(): LiveData<ApiResponse<RequestType>>

    @MainThread
    protected abstract fun onFetchFailed(envelope: Envelope?)
}