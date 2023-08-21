package com.swapnil.contlo.repository

import android.util.Log
import com.swapnil.contlo.repository.network.service.GithubNetworkService

class GithubRepository(private val githubNetworkService: GithubNetworkService) {
    private val TAG = "GithubRepository"
    suspend fun getAllClosedPRs() {
        Log.d(TAG, "getAllClosedPRs: ${githubNetworkService.getClosedPRs().data}")
    }
}