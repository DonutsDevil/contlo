package com.swapnil.contlo.repository.network.interceptors

import android.text.TextUtils
import com.swapnil.contlo.utility.NetworkConstants.Companion.HK_AUTHORIZATION
import com.swapnil.contlo.utility.NetworkUtility
import okhttp3.Interceptor
import okhttp3.Response

class HeadInjector: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        if(TextUtils.isEmpty(request.header(HK_AUTHORIZATION))) {
            builder.addHeader(HK_AUTHORIZATION, "Bearer ${NetworkUtility.getGithubToken()}")
        }
        return chain.proceed(builder.build())
    }
}