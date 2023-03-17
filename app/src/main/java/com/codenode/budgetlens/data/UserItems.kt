package com.codenode.budgetlens.data

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.utils.HttpResponseListener
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class UserItems {
    companion object {
        var pageNumber = 1

        private var httpResponseListener: HttpResponseListener? = null

        fun requestItemsFromAPI(
            viewItemRequestType: Int,
            context: Context,
            pageSize: Int,
            queryParams: String
        ) {
            httpResponseListener = context as Activity as HttpResponseListener
            val userItems = mutableListOf<Items>()
            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/pageNumber=${pageNumber}&pageSize=${pageSize}/" + queryParams
            var contentLoadedFromResponse = false

            val itemsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()
            itemsRequest.newCall(request).enqueue(object : Callback {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val responseString = JSONObject(responseBody.toString())

                                val pageList = responseString.getString("page_list")
                                val totalPrice = responseString.getDouble("total_price")

                                val items = JSONArray(pageList)

                                for (i in 0 until items.length()) {
                                    contentLoadedFromResponse = true
                                    val item = items.getJSONObject(i)
                                    val id = item.getInt("id")
                                    val name = item.getString("name")
                                    val price = item.getDouble("price")
                                    val scanDate = item.getString("scan_date")
                                    val itemCategoryName = item.getString("category_name")
                                    var month: String
                                    when (scanDate.substring(5, 7)) {
                                        "01" -> month = "JAN"
                                        "02" -> month = "FEB"
                                        "03" -> month = "MAR"
                                        "04" -> month = "APR"
                                        "05" -> month = "MAY"
                                        "06" -> month = "JUN"
                                        "07" -> month = "JUL"
                                        "08" -> month = "AUG"
                                        "09" -> month = "SEP"
                                        "10" -> month = "OCT"
                                        "11" -> month = "NOV"
                                        "12" -> month = "DEC"
                                        else -> {
                                            month = "NA"
                                        }

                                    }
                                    val displayDate =
                                        month + " " + scanDate.substring(8, 10)

                                    userItems.add(
                                        Items(
                                            id,
                                            name,
                                            price,
                                            displayDate,
                                            itemCategoryName
                                        )
                                    )
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                }
                                httpResponseListener?.onHttpSuccess(
                                    viewItemRequestType,
                                    userItems,
                                    totalPrice
                                )
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
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    httpResponseListener?.onHttpError()
                }
            })
        }
    }
}