package com.swapnil.contlo.repository

import android.content.Context
import android.util.Log
import com.swapnil.contlo.model.PullRequest
import com.swapnil.contlo.repository.network.service.GithubNetworkService
import com.swapnil.contlo.utility.Status

class GithubRepository(private val githubNetworkService: GithubNetworkService) {
    private val TAG = "GithubRepository"
    suspend fun getAllClosedPRs(context: Context): Status<List<PullRequest>> {
        val status = githubNetworkService.getClosedPRs(context)
        Log.d(TAG, "getAllClosedPRs: ${status.data}")
        return status
    }
}