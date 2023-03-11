package com.codenode.budgetlens.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.adapter.CalendarAdapter
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.CalendarBean
import com.codenode.budgetlens.utils.HttpUtils
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_calendar_list.*
import java.math.BigDecimal
import okhttp3.*
import java.io.IOException
import com.codenode.budgetlens.data.ReceiptSplitItem
import org.json.JSONObject


class CalendarListActivity : AppCompatActivity() {

    var madapter= CalendarAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_list)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)
       /* tv_split.setOnClickListener {

        }*/
        friends_list.adapter=madapter
        getData()
        madapter.setOnItemChildClickListener { adapter, view, position ->
            startActivity(Intent(this, ChooseFriendActivity::class.java))
        }
        tv_cancel.setOnClickListener {
            finish()
        }
        tv_sure.setOnClickListener {
            val intent = Intent()
            setResult(101, intent)
            finish()
        }
    }

    private fun getData() {
        val ids = intent.getIntExtra("ids", -1)
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/receipt/$ids/"

        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string() ?: return
                val jsonObject = JSONObject(json)
                val items = jsonObject.getJSONArray("items")
                val itemList = mutableListOf<ReceiptSplitItem>()
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val receiptSplitItem = ReceiptSplitItem()
                    receiptSplitItem.item_id = item.getInt("id")
                    receiptSplitItem.item_name = item.getString("name")
                    receiptSplitItem.item_price = item.getString("price")
                    receiptSplitItem.splitList = listOf()
                    itemList.add(receiptSplitItem)
                }

                runOnUiThread {
                    madapter.setNewInstance(itemList)
                    }

                }
        })
    }


}