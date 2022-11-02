package com.codenode.budgetlens.data

import android.content.Context
import android.util.Log
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.CountDownLatch

class UserReceipts {

    companion object {
        var userReceipts = mutableListOf<Receipts>()

        fun loadReceiptsFromAPI(context: Context): MutableList<Receipts> {

            // When reloading, make the list empty
            userReceipts.clear()

            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/"

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
                                val receipts = JSONArray(responseBody.toString())
                                for (i in 0 until receipts.length()) {
                                    val receipt = receipts.getJSONObject(i)
                                    val id = receipt.getInt("id")
                                    println("id $id")
                                    val merchant = receipt.getString("merchant")
                                    val scanDate = receipt.getString("scan_date")
                                    val receiptImage = receipt.getString("receipt_image")
                                    val location = receipt.getString("location")
                                    val total = receipt.getDouble("total")
                                    val tax = receipt.getDouble("tax")
                                    val tip = receipt.getDouble("tip")
                                    val coupon = receipt.getInt("coupon")
                                    val currency = receipt.getString("currency")
                                    val importantDates = receipt.getString("important_dates")
                                    userReceipts.add(Receipts(id, merchant, scanDate, receiptImage, location, total, tax, tip, coupon, currency, importantDates))
                                }
                                Log.i("Successful", "Successfully loaded receipts from API.")

                            } else {
                                Log.i("Error", "Something went wrong ${response.message} ${response.headers}")
                            }

                        } else {
                            Log.e("Error", "Something went wrong ${response.message} ${response.headers}")
                        }
                    }
                    countDownLatch.countDown();
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    countDownLatch.countDown();
                }
            })

            // wait for a response before returning
            countDownLatch.await();
            return userReceipts
        }
    }
}