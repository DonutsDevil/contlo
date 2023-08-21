package com.swapnil.contlo.repository.network.service

import com.swapnil.contlo.model.PullRequest
import com.swapnil.contlo.utility.Status

interface GithubNetworkService {

    suspend fun getClosedPRs(): Status<List<PullRequest>>
}