package com.swapnil.contlo.repository.network.serviceImpl

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class GithubNetworkServiceImpl(private val githubAPI: GithubAPI) : GithubNetworkService {
    private val TAG = "GithubNetworkServiceImp"
    override suspend fun getClosedPRs(context: Context): Status<List<PullRequest>> {
        val responseBody = githubAPI.getAllClosedPRs()
        val jsonString = getJsonString(responseBody)
        Log.d(TAG, "getClosedPRs: jsonString: $jsonString")
        val pullRequestList = getListOfClosedPRs(jsonString)
        loadImages(pullRequestList, context)
        return pullRequestList
    }

    private fun loadImages(pullRequestState: Status<List<PullRequest>>, context: Context) {

        if (pullRequestState is Status.Success) {
            pullRequestState.data!!.forEach { pullRequest ->
                Glide.with(context)
                    .load(pullRequest.user.avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
            }
        }
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
                val createdAt = convertDateTime(jsonObject.getString("created_at"))
                val closedAt = convertDateTime(jsonObject.getString("closed_at"))
                val number = jsonObject.getInt("number")
                val prUrl = jsonObject.getString("html_url")

                val userObject = jsonObject.getJSONObject("user")
                val userName = userObject.getString("login")
                val userAvatarUrl = userObject.getString("avatar_url")

                if (title.isNullOrEmpty().not() && createdAt?.isEmpty() == false
                         && closedAt?.isEmpty() == false && userName.isNullOrEmpty()
                        .not() && userAvatarUrl.isNullOrEmpty().not()
                ) {
                    val user = User(userName, userAvatarUrl)
                    val pullRequest = PullRequest(title, createdAt, closedAt, user, number, prUrl)
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

    fun convertDateTime(inputDateTime: String?): String? {
        if (inputDateTime == null) {
            return null
        }
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(inputDateTime)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
}