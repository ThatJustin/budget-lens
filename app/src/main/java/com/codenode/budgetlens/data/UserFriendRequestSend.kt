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
class UserFriendRequestSend {
    companion object{
        var userFriendRequestSend = mutableListOf<Friends>()
        var pageNumber = 1

        fun loadFriendRequestSendFromAPI(context: Context, pageSize: Int, additionalData:String): MutableList<Friends> {


           val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friend/request/"
//            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friends/pageNumber=${UserFriends.pageNumber}&pageSize=${pageSize}/"+additionalData
            var contentLoadedFromResponse = false

            val friendsRequestSendRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            val countDownLatch = CountDownLatch(1)
            friendsRequestSendRequest.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val friendRequestSendObjects= JSONObject(responseBody.toString()).getString("requests_sent")
                                val friendRequestSend = JSONArray(friendRequestSendObjects)
                                userFriendRequestSend = mutableListOf<Friends>()
                                for (i in 0 until friendRequestSend.length()) {
                                    contentLoadedFromResponse = true
                                    val friendRequestSend = friendRequestSend.getJSONObject(i)
                                    val userId = friendRequestSend.getInt("id")
                                    val firstName = friendRequestSend.getString("first_name")
                                    val lastName = friendRequestSend.getString("last_name")
                                    val email= friendRequestSend.getString("email")
                                    val initial = firstName[0]
                                    userFriendRequestSend.add(
                                        Friends(
                                                userId,
                                                firstName,
                                                lastName,
                                                email,
                                                initial,

                                            )
                                        )
                                    }
                                val friendInviteSendObjects= JSONObject(responseBody.toString()).getString("invites_sent")
                                val friendInviteSend = JSONArray(friendInviteSendObjects)
                                for (i in 0 until friendInviteSend.length()) {
                                    val friendInviteSend = friendInviteSend.getJSONObject(i)
                                    val email = friendInviteSend.getString("email")
                                    val initial = email[0]
                                    userFriendRequestSend.add(
                                        Friends(
                                            null,
                                            null,
                                            null,
                                            email,
                                            initial,
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
            return userFriendRequestSend
        }
    }
}