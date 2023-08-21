package com.swapnil.contlo.repository.network.service

interface GithubNetworkService {

    suspend fun getClosedPRs()
}