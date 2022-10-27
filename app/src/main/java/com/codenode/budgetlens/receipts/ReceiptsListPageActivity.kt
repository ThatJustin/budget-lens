package com.codenode.budgetlens.receipts

import android.util.Log
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ReceiptsListPageActivity : AppCompatActivity() {
    private var receiptsList: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/"

        val receiptsPost = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .addHeader("Content-Type", "application/json")
            .build()

        receiptsPost.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val jsonObject = JSONObject(responseBody.toString())
                            val receipts = jsonObject.getJSONArray("receipts")
                            for(i in 0 until receipts.length()) {
                                val receipt = receipts.getJSONObject(i)
                                val receiptImageURL = receipt.getString("receipt_image")
                                val scanDate = receipt.getString("scan_date")
                                d("receipt image URL link", "scan date: $receiptImageURL, $scanDate")
                            }
                            Log.i("Successful", "Successfully loaded profile from API.")
                        }
                        else {
                            Log.i("Error", "Something went wrong${response.body?.string()}")
                        }
                    } else {
                        println("failed to load receipts from API.")
                        Log.e(
                            "Error", "Something went wrong${response.body?.string()} ${response.message} ${response.headers}"
                        )
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })

        receiptsList.apply {
            layoutManager = LinearLayoutManager(this@ReceiptsListPageActivity)
            adapter = ReceiptsRecyclerViewAdapter()
        }
    }
}