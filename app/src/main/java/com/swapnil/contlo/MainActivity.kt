package com.swapnil.contlo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.swapnil.contlo.repository.GithubRepository
import com.swapnil.contlo.repository.network.ContloNetwork
import com.swapnil.contlo.repository.network.service.GithubAPI
import com.swapnil.contlo.repository.network.serviceImpl.GithubNetworkServiceImpl
import com.swapnil.contlo.utility.NetworkConstants
import com.swapnil.contlo.utility.Status
import com.swapnil.contlo.viewmodel.ContloFactory
import com.swapnil.contlo.viewmodel.ContloViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var contloViewMode: ContloViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val retrofit = ContloNetwork().getRetrofitInstance(NetworkConstants.BASE_URL)
        retrofit?.let {
            val githubApi = it.create(GithubAPI::class.java)
            val githubNetworkServiceImpl = GithubNetworkServiceImpl(githubApi)
            val githubRepository = GithubRepository(githubNetworkServiceImpl)
            contloViewMode = ViewModelProvider(
                this,
                ContloFactory(githubRepository)
            )[ContloViewModel::class.java]
        }
        observeClosePrList()
        contloViewMode.getAppClosedPRs()
    }

    private fun observeClosePrList() {
        contloViewMode.pullRequestStatus.observe(this) { status ->
            when (status) {
                is Status.Error -> {
                    Log.e(TAG, "observeClosePrList: Error: ${status.errorMsg}")
                    Toast.makeText(this, status.errorMsg, Toast.LENGTH_SHORT).show()
                }
                is Status.Loading -> TODO()
                is Status.Success -> {
                    Log.d(TAG, "observeClosePrList: success: ${status.data}")
                }
            }
        }
    }
}