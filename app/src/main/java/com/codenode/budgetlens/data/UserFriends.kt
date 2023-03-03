package com.codenode.budgetlens.data

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.friends.FriendsPageActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

//Im not sure about this class. since all friends are users,
// friends are the relation between, just in case this class is needed so i created like this
class UserFriends {
    companion object {
        var userFriends = mutableListOf<Friends>()
        var pageNumber = 1

        fun loadFriendsFromAPI(
            context: Context,
            pageSize: Int,
            additionalData: String
        ): MutableList<Friends> {


            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friend/"
//            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friends/pageNumber=${UserFriends.pageNumber}&pageSize=${pageSize}/"+additionalData
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
                                val friendsObjects =
                                    JSONObject(responseBody.toString()).getString("response")
                                val friends = JSONArray(friendsObjects)
                                userFriends = mutableListOf<Friends>()
                                for (i in 0 until friends.length()) {
                                    contentLoadedFromResponse = true
                                    val friends = friends.getJSONObject(i)
                                    val userId = friends.getInt("id")
                                    val firstName = friends.getString("first_name")
                                    val lastName = friends.getString("last_name")
                                    val email = friends.getString("email")
                                    val initial = firstName[0]
                                    userFriends.add(
                                        Friends(
                                            userId,
                                            firstName,
                                            lastName,
                                            email,
                                            initial,

                                            )
                                    )
                                }

                                if (contentLoadedFromResponse) {
                                    Log.i("im here bruh", "hahahhahahahhahahahah")
                                    //Log.i("if",pageNumber.toString())
                                    //pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded Friends from API.")
                            } else {
                                Log.i(
                                    "Error",
                                    "Something went wrong ${response.message} ${response.headers}"
                                )
                            }
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
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

        fun sendFriendRequest(context: Context, emailInput: EditText, onSuccess: ((String) -> Unit)? =null,
                              onFailed: ((String) -> Unit)? =null) {
            // TODO: change to correct one l8r
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friend/request/"
            var emailAddress = emailInput.text.toString()
            val registrationPost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            val body = ("{\r\n" +
                    "    \"email\": \"${emailInput.text}\"\r\n" +
                    "}").trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()

            registrationPost.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val strBody =response.body?.string()
                            if (strBody.isNullOrBlank()){
                                onSuccess?.invoke("Invite sent successfully")
                                return
                            }
                            val jsonObj = JSONObject(strBody)
                            var apiMessage = jsonObj.getString("response").trim()
                            if(apiMessage.endsWith(".")){
                                apiMessage = apiMessage.substring(0, apiMessage.length-1)
                            }
                            onSuccess?.invoke(apiMessage.plus(": $emailAddress"))
                            Log.i("Successful", "$strBody $emailAddress")
                        } else {
                            val message = response.body?.string()?.noQuote() ?: "Send invite has failed"
                            onFailed?.invoke(message)
                            Log.e(
                                "Error",
                                "Something went wrong $message ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }
    }
}


fun String.noQuote(): String{
    val len = this.length-1
    return this.trim().substring(1, len)
}
