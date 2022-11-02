package com.codenode.budgetlens.data

import android.content.Context
import android.util.Log
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import java.io.IOException

class UserReceipts {

    companion object {
        val userReceipts = Receipts()

        fun loadReceiptsFromAPI(context: Context): List<Receipts> {

            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/"

            val receiptsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()

            receiptsRequest.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body
                    Log.d("Receipts", body.toString())
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
            })
            return listOf()
        }
    }
}