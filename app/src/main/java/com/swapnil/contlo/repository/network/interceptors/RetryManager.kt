package com.swapnil.contlo.repository.network.interceptors

import android.os.SystemClock
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor responsible for retrying the API request if failure after 2 secs
 * @param maxAttempts: defines number of time [com.swapnil.contlo.repository.network.interceptors.RetryManager] should retry
 */
class RetryManager(private val maxAttempts: Int): Interceptor {
    private val TAG = "RetryManager"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val url = request.url().toString()
        var response: Response? = null
        var attempts = 0
        while (canBeRetried(response) && attempts <  maxAttempts) {
            attempts++
            Log.d(TAG, "request ID:  ${chain.hashCode()}, attempt($attempts/$maxAttempts): $url")
            try {
                response = chain.proceed(chain.request())
                Log.d(TAG, "request ID:  ${chain.hashCode()}, attempt($attempts/$maxAttempts): HTTP: ${response?.code()} -> ${response?.request()?.method()} : $url")
            } catch (e: IOException) {
                Log.e(TAG, "request ID:${chain.hashCode()}, response($attempts/$maxAttempts): $url", e)
                if (attempts == maxAttempts && response == null) {
                    throw e
                }
            }

            if ((response == null || !response.isSuccessful) && attempts <= maxAttempts) {
                try {
                    if (canBeRetried(response)) {
                        Log.d(TAG, "request ID:  ${chain.hashCode()}, waiting for 2 sec before trying again.")
                        SystemClock.sleep(2000)
                    } else {
                        Log.d(TAG, "intercept: Not Retrying this request")
                        break
                    }
                } catch (e: InterruptedException) {
                    Log.e(TAG, "request ID:${chain.hashCode()}, Failed to sleep before the next retry", e)
                }
            }
        }

        if (canBeRetried(response)) {
            Log.d(TAG, "request ID:  ${chain.hashCode()}, attempt($attempts/$maxAttempts): $url")
            try {
                response = chain.proceed(chain.request())
                Log.d(TAG, "request ID:  ${chain.hashCode()}, attempt($attempts/$maxAttempts): HTTP: ${response?.code()} -> ${response?.request()?.method()} : $url")
            } catch (e: IOException) {
                Log.e(TAG, "request ID:${chain.hashCode()}, response($attempts/$maxAttempts): $url", e)
                throw e
            }
        }
        return response
    }


    private fun canBeRetried(response: Response?): Boolean {
        val requestFailed = response == null || !response.isSuccessful
        if (!requestFailed) {
            return false
        }

        if (response != null) {
            val responseCode = response.code()
            return retryAllowedForCode(responseCode)
        }
        return true
    }

    private fun retryAllowedForCode(code: Int) : Boolean {
        var isAllowed = false
        when (code) {
            429, 500, 501, 502, 503, 504 -> isAllowed = true
        }
        return isAllowed
    }

}