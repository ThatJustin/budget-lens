package com.codenode.budgetlens.data

import android.content.Context
import android.util.Log
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

//Im not sure about this class. since all friends are users,
// friends are the relation between, just in case this class is needed so i created like this
class UserFriends {
    companion object{
        var userFriends = mutableListOf<Friends>()
        var pageNumber = 1

        fun loadFriendsFromAPI(context: Context, pageSize: Int, additionalData:String): MutableList<Friends> {


            // Todo: change the URL
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/pageNumber=${UserFriends.pageNumber}&pageSize=${pageSize}/"+additionalData
            var contentLoadedFromResponse = false

            val friendsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            val countDownLatch = CountDownLatch(1)
            friendsRequest.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val pageList = JSONObject(responseBody.toString()).getString("page_list")
                                val friends = JSONArray(pageList)
                                for (i in 0 until friends.length()) {
                                    contentLoadedFromResponse = true
                                    val friends = friends.getJSONObject(i)
                                    val userId = friends.getInt("id")
                                    val friendName = friends.getString("name")
                                    val tradeRelation = friends.getString("trade_relation")
                                    val tradeAmount = friends.getDouble("trade_amount")
                                    userFriends.add(Friends(userId,friendName,tradeRelation,tradeAmount))
                                }
                                //?? i copy paste this who wrote it lmao
                                if (contentLoadedFromResponse) {
                                    Log.i("im here bruh","hahahhahahahhahahahah")
                                    Log.i("if",pageNumber.toString())
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded Friends from API.")
                            } else {
                                Log.i("Error", "Something went wrong ${response.message} ${response.headers}")
                            }
                        } else {
                            Log.e("Error", "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    }
                    countDownLatch.countDown()
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    countDownLatch.countDown()
                }
            })

            // wait for a response before returning
            countDownLatch.await()
            return userFriends
        }
    }
}