package com.codenode.budgetlens.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.receipts.ReceiptMenu
import com.codenode.budgetlens.utils.HttpResponseListener
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class UserReceipts {


    companion object {
        var pageNumber = 1
        private var httpResponseListener: HttpResponseListener? = null


        private val receiptsRequest = OkHttpClient()

        fun filterReceiptsMenu(context: Activity, onSuccess: (ReceiptMenu) -> Unit,
                           onFailure: (String) -> Unit){
            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/filters/"
            receiptsRequest.newCall(request(url = url, context)).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val json = response.body?.string()
                            val receiptMenu = ReceiptMenu().toReceiptMenu(json)
                            context.runOnUiThread {
                                onSuccess.invoke(receiptMenu)
                            }
                        } else {
                            context.runOnUiThread {
                                onFailure.invoke("Something went wrong ${response.message} ${response.headers}")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onFailure.invoke("Something went wrong")

                }
            })
        }


        private fun request(url: String, context: Context): Request{
            println("url $url")
            return Request.Builder().url(url).method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json").build()
        }

        //TODO move this to another thread
        fun requestReceiptsFromAPI(
            viewItemRequestType: Int, context: Context, pageSize: Int, queryParams: String
        ) {
            httpResponseListener = context as Activity as HttpResponseListener
            val userReceipts = mutableListOf<Receipts>()
            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/pageNumber=${pageNumber}&pageSize=${pageSize}/" + queryParams
            var contentLoadedFromResponse = false

            receiptsRequest.newCall(request(url = url, context)).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            Log.d("UserReciepts", "responseBody: $responseBody")
                            if (responseBody != null) {
                                val pageList =
                                    JSONObject(responseBody.toString()).getString("page_list")
                                val receipts = JSONArray(pageList)
                                for (i in 0 until receipts.length()) {
                                    contentLoadedFromResponse = true
                                    val receipt = receipts.getJSONObject(i)
                                    val id = receipt.getInt("id")
                                    val merchant = receipt.getString("merchant")
                                    val scanDate = receipt.getString("scan_date").substring(0, 16).replace("T", " ")
                                    val receiptImage = receipt.getString("receipt_image")
                                    val location = receipt.getString("location")
                                    val total = receipt.getDouble("total")
                                    val tax = receipt.getDouble("tax")
                                    val tip = receipt.getDouble("tip")
                                    val coupon = receipt.getDouble("coupon")
                                    val currency = receipt.getString("currency")
                                    userReceipts.add(
                                        Receipts(
                                            id,
                                            merchant,
                                            scanDate,
                                            receiptImage,
                                            location,
                                            total,
                                            tax,
                                            tip,
                                            coupon,
                                            currency
                                        )
                                    )
                                    println("Adding receipt to list with ID $id")
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                    println("pageNumber $pageNumber")
                                }
                                httpResponseListener?.onHttpSuccess(
                                    viewItemRequestType, userReceipts
                                )
                                Log.i("Successful", "Successfully loaded receipts from API.")
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