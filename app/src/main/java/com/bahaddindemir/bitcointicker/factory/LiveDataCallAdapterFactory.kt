package com.bahaddindemir.bitcointicker.factory

import androidx.lifecycle.LiveData
import com.bahaddindemir.bitcointicker.data.model.ApiResponse
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<out Annotation>,
                     retrofit: Retrofit): CallAdapter<*, *>? {

        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterizec")
        }

        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Type>(bodyType)
    }
}

class LiveDataCallAdapter<R>(private val responseType: Type) :
      CallAdapter<R, LiveData<ApiResponse<R>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(ApiResponse(response))
                        }

                        override fun onFailure(call: Call<R>, t: Throwable) {
                            postValue(ApiResponse(t))
                        }
                    })
                }
            }
        }
    }
}