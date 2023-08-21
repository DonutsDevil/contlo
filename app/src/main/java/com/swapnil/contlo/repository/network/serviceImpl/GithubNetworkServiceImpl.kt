package com.swapnil.contlo.repository.network.serviceImpl

import android.util.Log
import com.swapnil.contlo.model.PullRequest
import com.swapnil.contlo.model.User
import com.swapnil.contlo.repository.network.service.GithubAPI
import com.swapnil.contlo.repository.network.service.GithubNetworkService
import com.swapnil.contlo.utility.Status
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class GithubNetworkServiceImpl(private val githubAPI: GithubAPI) : GithubNetworkService {
    private val TAG = "GithubNetworkServiceImp"
    override suspend fun getClosedPRs(): Status<List<PullRequest>> {
        val responseBody = githubAPI.getAllClosedPRs()
        val jsonString = getJsonString(responseBody)
        Log.d(TAG, "getClosedPRs: jsonString: $jsonString")
        return getListOfClosedPRs(jsonString)
    }

    private fun getJsonString(responseBody: ResponseBody?): String? {
        if (responseBody == null) {
            Log.d(TAG, "getJsonString: Response Body is null")
            return null
        }
        val inputStream = responseBody.byteStream()
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonBuilder = StringBuilder()
        var line: String?
        var jsonString: String? = null
        try {
            while (reader.readLine().also { line = it } != null) {
                jsonBuilder.append(line)
            }
            jsonString = jsonBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return jsonString

    }

    private fun getListOfClosedPRs(jsonString: String?): Status<List<PullRequest>> {
        if (jsonString.isNullOrEmpty()) {
            Log.d(TAG, "getListOfClosedPRs: jsonString is empty / empty: $jsonString")
            return Status.Error("Json is not correct")
        }
        return try {
            val pullRequestList = mutableListOf<PullRequest>()
            val jsonArray = JSONArray(jsonString)
            for (index in 0 until jsonArray.length() step 1) {
                val jsonObject = jsonArray.getJSONObject(index)
                val title = jsonObject.getString("title")
                val createdAt = jsonObject.getString("created_at")
                val closedAt = jsonObject.getString("closed_at")

                val userObject = jsonObject.getJSONObject("user")
                val userName = userObject.getString("login")
                val userAvatarUrl = userObject.getString("avatar_url")

                if (title.isNullOrEmpty().not() && createdAt.isNullOrEmpty()
                        .not() && closedAt.isNullOrEmpty().not() && userName.isNullOrEmpty()
                        .not() && userAvatarUrl.isNullOrEmpty().not()
                ) {
                    val user = User(userName, userAvatarUrl)
                    val pullRequest = PullRequest(title, createdAt, closedAt, user)
                    pullRequestList.add(pullRequest)
                }

            }
            Status.Success(pullRequestList)
        } catch (e: IOException) {
            e.printStackTrace()
            Status.Error("interruption occurred when fetching from internet")
        } catch (e: JSONException) {
            e.printStackTrace()
            Status.Error("interruption occurred parsing information from internet")
        }
    }
}