package com.swapnil.contlo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.swapnil.contlo.repository.GithubRepository
import com.swapnil.contlo.repository.network.ContloNetwork
import com.swapnil.contlo.repository.network.service.GithubAPI
import com.swapnil.contlo.repository.network.serviceImpl.GithubNetworkServiceImpl
import com.swapnil.contlo.utility.NetworkConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        // TODO: Remove it from here, since added for testing.
        val retrofit = ContloNetwork().getRetrofitInstance(NetworkConstants.BASE_URL)
        retrofit?.let {
            val githubApi = it.create(GithubAPI::class.java)
            val githubNetworkServiceImpl = GithubNetworkServiceImpl(githubApi)
            val githubRepository = GithubRepository(githubNetworkServiceImpl)
            GlobalScope.launch(Dispatchers.IO) {
                githubRepository.getAllClosedPRs()
            }
        }
    }
}