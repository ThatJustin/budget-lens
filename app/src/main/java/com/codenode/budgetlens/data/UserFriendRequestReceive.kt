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
class UserFriendRequestReceive {
    companion object{
        var userFriendRequestReceive = mutableListOf<FriendRequestReceive>()
        var pageNumber = 1

        fun loadFriendRequestReceiveFromAPI(context: Context, pageSize: Int, additionalData:String): MutableList<FriendRequestReceive> {


           val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/requests_received/"
//            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friends/pageNumber=${UserFriends.pageNumber}&pageSize=${pageSize}/"+additionalData
            var contentLoadedFromResponse = false

            val friendsRequestReceiveRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            val countDownLatch = CountDownLatch(1)
            friendsRequestReceiveRequest.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val friendRequestReceiveObjects= JSONObject(responseBody.toString()).getString("response")
                                val friendRequestReceive = JSONArray(friendRequestReceiveObjects)
                                userFriendRequestReceive = mutableListOf<FriendRequestReceive>()
                                for (i in 0 until friendRequestReceive.length()) {
                                    contentLoadedFromResponse = true
                                    val friendRequestReceive = friendRequestReceive.getJSONObject(i)
                                    val userId = friendRequestReceive.getInt("id")
                                    val firstName = friendRequestReceive.getString("first_name")
                                    val lastName = friendRequestReceive.getString("last_name")
                                    val email= friendRequestReceive.getString("email")
                                    val initial = firstName[0]
                                    val isConfirmed = friendRequestReceive.getBoolean("confirm")
                                    userFriendRequestReceive.add(
                                        FriendRequestReceive(
                                                userId,
                                                firstName,
                                                lastName,
                                                email,
                                                initial,
                                                isConfirmed

                                            )
                                        )
                                    }

                                if (contentLoadedFromResponse) {
                                    Log.i("im here bruh","hahahhahahahhahahahah")
                                    //Log.i("if",pageNumber.toString())
                                    //pageNumber++
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
            return userFriendRequestReceive
        }
    }
}