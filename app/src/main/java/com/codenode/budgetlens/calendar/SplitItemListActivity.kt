package com.codenode.budgetlens.calendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.adapter.ItemSplitListApp
import com.codenode.budgetlens.adapter.SplitItemListAdapter
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Friends
import kotlinx.android.synthetic.main.activity_split_item_list.*
import okhttp3.*
import java.io.IOException
import com.codenode.budgetlens.data.ReceiptSplitItem
import com.codenode.budgetlens.data.UserFriends
import kotlinx.android.synthetic.main.activity_split_item_list.*
import org.json.JSONObject


class SplitItemListActivity : AppCompatActivity() {

    var madapter = SplitItemListAdapter()
    var item_list = mutableListOf<ReceiptSplitItem>()
    private lateinit var friendList: MutableList<Friends>
    private val REQUEST_CODE_CHOOSE_FRIENDS = 81

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_item_list)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)
        /* tv_split.setOnClickListener {

         }*/
        friends_list.adapter = madapter
        getData()
        friendList = UserFriends.loadFriendsFromAPI(this, 5, "")
        madapter.friendList = friendList
        madapter.setOnItemChildClickListener { adapter, view, position ->
            item_list = madapter.itemList as MutableList<ReceiptSplitItem>
            val app = application as ItemSplitListApp
            app.itemList = item_list
            val clickedItem = item_list[position]
            val selected_item_id = clickedItem.item_id ?: -1
            val selectedList = intent.getIntegerArrayListExtra("selectedList")
            val intent = Intent(this@SplitItemListActivity, ChooseFriendActivity::class.java)
            intent.putExtra("selectedList", selectedList?.let { ArrayList(it) })
            intent.putExtra("selected_item_id", selected_item_id)
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_FRIENDS)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if the result is from the ChooseFriendActivity and the result code is Activity.RESULT_OK
        if (requestCode == REQUEST_CODE_CHOOSE_FRIENDS && resultCode == Activity.RESULT_OK) {

            val app = application as ItemSplitListApp
            item_list = app.itemList!!
            runOnUiThread {
                madapter.itemList = item_list
                madapter.setNewInstance(item_list)
            }

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
                    madapter.itemList = itemList
                    madapter.setNewInstance(itemList)
                }

            }
        })
    }


}