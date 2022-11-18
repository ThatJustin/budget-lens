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

class UserItems {
    companion object{
        var userItems = mutableListOf<Items>()
        var pageNumber = 1

        //TODO move this to another thread
        fun loadItemsFromAPI(context: Context, pageSize: Int, additionalData:String): MutableList<Items> {


            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/pageNumber=${UserItems.pageNumber}&pageSize=${pageSize}/"+additionalData
            var contentLoadedFromResponse = false

            val receiptsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            val countDownLatch = CountDownLatch(1)
            receiptsRequest.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val pageList = JSONObject(responseBody.toString()).getString("page_list")
                                val items = JSONArray(pageList)
                                for (i in 0 until items.length()) {
                                    contentLoadedFromResponse = true
                                    val item = items.getJSONObject(i)
                                    val id = item.getInt("id")
                                    val name = item.getString("name")
                                    val price = item.getDouble("price")
                                    val importantDates = item.getString("important_dates")
                                    userItems.add(Items(id,name,price,importantDates))
                                }
                                if (contentLoadedFromResponse) {
                                    Log.i("im here bruh","hahahhahahahhahahahah")
                                    Log.i("if",pageNumber.toString())
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded items from API.")
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
            return userItems
        }
    }
}