package com.swapnil.contlo.repository.network.service

import android.content.Context
import com.swapnil.contlo.model.PullRequest
import com.swapnil.contlo.utility.Status

interface GithubNetworkService {

    suspend fun getClosedPRs(context: Context): Status<List<PullRequest>>
}