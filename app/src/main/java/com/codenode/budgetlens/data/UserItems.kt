package com.codenode.budgetlens.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class UserItems {
    companion object {
        var userItems = mutableListOf<Items>()
        var pageNumber = 1
        var totalCost = 0.0


        //TODO move this to another thread
        fun loadItemsFromAPI(
            context: Context,
            pageSize: Int,
            additionalData: String
        ): Pair<MutableList<Items>, Double> {


            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/pageNumber=${pageNumber}&pageSize=${pageSize}/" + additionalData
            var contentLoadedFromResponse = false

            val itemsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            val countDownLatch = CountDownLatch(1)
            itemsRequest.newCall(request).enqueue(object : Callback {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val pageList =
                                    JSONObject(responseBody.toString()).getString("page_list")
                                val items = JSONArray(pageList)

                                try{
                                    totalCost =
                                        JSONObject(responseBody.toString()).getString("total Cost")
                                            .toDouble()
                                }catch (e:Exception){}


                                for (i in 0 until items.length()) {
                                    contentLoadedFromResponse = true
                                    val item = items.getJSONObject(i)
                                    val id = item.getInt("id")
                                    val name = item.getString("name")
                                    val price = item.getDouble("price")
                                    val importantDates = item.getString("important_dates")
                                    val scanDate = item.getString("scan_date")
                                    val displayDate = scanDate.substring(5,7)+" "+scanDate.substring(8,10)

                                    userItems.add(
                                        Items(
                                            id,
                                            name,
                                            price,
                                            importantDates,
                                            displayDate
                                        )
                                    )
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded items from API.")
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
            return Pair(userItems, totalCost)
        }

    }
}