package com.swapnil.contlo.repository.network

import android.text.TextUtils
import android.util.Log
import com.swapnil.contlo.repository.network.interceptors.HeadInjector
import com.swapnil.contlo.repository.network.interceptors.RetryManager
import com.swapnil.contlo.utility.NetworkConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ContloNetwork {
    private val TAG = "ContloNetwork"
    private val INSTANCES = mutableMapOf<String, Retrofit>()

    /**
     * Create a Retrofit instance, if already created will return the created one rather then creating new
     * @param baseUrl: url used as key which retrofit instance is created
     * @return retrofit instance
     */
    fun getRetrofitInstance(baseUrl: String): Retrofit? {
        if (TextUtils.isEmpty(baseUrl)) {
            Log.d(TAG, "getRetrofitInstance: baseUrl is empty")
            return null
        }
        return synchronized(INSTANCES) {
            if (INSTANCES.containsKey(baseUrl)) {
                return@synchronized INSTANCES[baseUrl]
            } else {
                val client = OkHttpClient.Builder()
                client.addInterceptor(HeadInjector())
                client.addInterceptor(RetryManager(NetworkConstants.HTTP_RETRY_MAX_ATTEMPTS))
                client.connectTimeout(2, TimeUnit.MINUTES)
                client.readTimeout(2, TimeUnit.MINUTES)
                val instance = getInstance(baseUrl, client)
                INSTANCES[baseUrl] = instance
                return@synchronized instance
            }
        }
    }

    private fun getInstance(baseUrl: String, client: OkHttpClient.Builder): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
    }
}