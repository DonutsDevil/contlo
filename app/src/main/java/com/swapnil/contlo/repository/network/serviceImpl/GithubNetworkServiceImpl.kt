package com.swapnil.contlo.repository.network.serviceImpl

import com.swapnil.contlo.repository.network.service.GithubAPI
import com.swapnil.contlo.repository.network.service.GithubNetworkService

class GithubNetworkServiceImpl(githubAPI: GithubAPI): GithubNetworkService {

    override suspend fun getClosedPRs() {
        TODO("Not yet implemented")
    }
}