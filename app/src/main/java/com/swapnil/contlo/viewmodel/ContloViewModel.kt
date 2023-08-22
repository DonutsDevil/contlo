package com.swapnil.contlo.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.swapnil.contlo.model.PullRequest
import com.swapnil.contlo.repository.GithubRepository
import com.swapnil.contlo.utility.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContloViewModel(val githubRepository: GithubRepository): ViewModel() {
    private val _pullRequestList = MutableLiveData<Status<List<PullRequest>>>()
    val pullRequestStatus: LiveData<Status<List<PullRequest>>>
        get() = _pullRequestList


    fun getAppClosedPRs(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = githubRepository.getAllClosedPRs(context.applicationContext)
            _pullRequestList.postValue(status)
        }
    }

}

class ContloFactory(private val githubRepository: GithubRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContloViewModel(githubRepository) as T
    }
}