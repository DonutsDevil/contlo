package com.swapnil.contlo.repository

import android.util.Log
import com.swapnil.contlo.model.PullRequest
import com.swapnil.contlo.repository.network.service.GithubNetworkService
import com.swapnil.contlo.utility.Status

class GithubRepository(private val githubNetworkService: GithubNetworkService) {
    private val TAG = "GithubRepository"
    suspend fun getAllClosedPRs(): Status<List<PullRequest>> {
        val status = githubNetworkService.getClosedPRs()
        Log.d(TAG, "getAllClosedPRs: ${status.data}")
        return status
    }
}