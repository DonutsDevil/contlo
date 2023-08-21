package com.swapnil.contlo.repository.network.service

import com.swapnil.contlo.utility.NetworkConstants
import com.swapnil.contlo.utility.NetworkUtility
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header

interface GithubAPI {
    @GET("pulls?state=closed")
    suspend fun getAllClosedPRs(
        @Header(NetworkConstants.HK_AUTHORIZATION) authToken: String = NetworkUtility.getGithubToken()
    ): ResponseBody?
}