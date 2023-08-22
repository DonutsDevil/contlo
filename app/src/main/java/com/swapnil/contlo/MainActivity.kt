package com.swapnil.contlo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swapnil.contlo.repository.GithubRepository
import com.swapnil.contlo.repository.network.ContloNetwork
import com.swapnil.contlo.repository.network.service.GithubAPI
import com.swapnil.contlo.repository.network.serviceImpl.GithubNetworkServiceImpl
import com.swapnil.contlo.utility.NetworkConstants
import com.swapnil.contlo.utility.Status
import com.swapnil.contlo.utility.adapters.ClickListener
import com.swapnil.contlo.utility.adapters.PullRequestAdapter
import com.swapnil.contlo.viewmodel.ContloFactory
import com.swapnil.contlo.viewmodel.ContloViewModel

class MainActivity : AppCompatActivity(), ClickListener {
    private val TAG = "MainActivity"
    private lateinit var contloViewMode: ContloViewModel

    private lateinit var pullRequestAdapter: PullRequestAdapter
    private lateinit var rvPullRequest: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
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
        setUpRvPullRequest()
        observeClosePrList()
        contloViewMode.getAppClosedPRs(this)
    }

    private fun setUpRvPullRequest() {
        pullRequestAdapter = PullRequestAdapter(this)
        rvPullRequest.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPullRequest.adapter = pullRequestAdapter
    }

    private fun initView() {
        rvPullRequest = findViewById(R.id.rv_pull_request)
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
                    if (status.data?.isEmpty() != true) {
                        pullRequestAdapter.submitList(status.data)
                    }
                }
            }
        }
    }

    override fun onPullRequestClicked(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setPackage("com.android.chrome")

        // Check if Chrome is installed, otherwise use a default browser
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // If Chrome is not installed, use a default browser
            intent.setPackage(null)
            startActivity(intent)
        }
    }
}