package com.codenode.budgetlens.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import org.json.JSONArray
import java.util.concurrent.CountDownLatch

class UserImportantDates {
    companion object {
        var userImportantDates = mutableListOf<ImportantDates>()

        /**
         * Updates the star when the user clicks on the star Image of the given field.
         * The API is called in the recycler view for the Category `CategoryRecyclerViewAdapter`
         *
         * `category`: The category that the user clicked the star for
         *
         * `imageStar`: The ImageView of the star in order for the star to be updated
         *
         */


        fun loadImportantDatesFromAPI(
            context: Context,
            itemId: String?
        ): MutableList<ImportantDates> {
            //get the important dates data
            val importantDateUrl =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/important_dates/${itemId}/"
            val datesRequest = OkHttpClient()

            // There is no request body for this PUT method, but the request builder requires
            // one which is why this body is created with empty `addFormDataPart`

            val dateRequestBody = Request.Builder()
                .url(importantDateUrl)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            val countDownLatch = CountDownLatch(1)
            datesRequest.newCall(dateRequestBody).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful) {
                            if (responseBody != null) {
                                val importantDatesListTemp = JSONArray(responseBody.toString())
                                for (i in 0 until importantDatesListTemp.length()) {
                                    val date = importantDatesListTemp.getJSONObject(i)
                                    val id = date.getInt("id")
                                    val user = date.getInt("user")
                                    val item = date.getInt("item")
                                    val dateString = date.getString("date")
                                    val description = date.getString("description")
                                    userImportantDates.add(
                                        ImportantDates(
                                            id,
                                            dateString,
                                            description,
                                            user,
                                            item
                                        )
                                    )
                                }


                            }

                            Log.i(
                                "Successful",
                                "Displayed Category string list successfully${responseBody}"
                            )
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong${responseBody} ${response.message} ${response.headers}"
                            )
                        }
                    }
                    countDownLatch.countDown()
                }
            })

            // wait for a response before returning
            countDownLatch.await()
            Log.d("Debug List", userImportantDates.toString())
            return userImportantDates
        }

        fun deleteImportantDatesFromAPI(
            context: Context,
            date: ImportantDates
        ) {

            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/important_dates/delete/${date.id}/"

            val toggleStarUpdate = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .method("DELETE", body = null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()

            toggleStarUpdate.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                @SuppressLint("NotifyDataSetChanged")
                @Suppress("Thread")
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful) {
                            // Remove the category from the list in the frontend list
                            userImportantDates.remove(date)
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong${responseBody} ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }


    }
}