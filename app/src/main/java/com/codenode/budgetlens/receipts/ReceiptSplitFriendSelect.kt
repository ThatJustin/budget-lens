package com.codenode.budgetlens.receipts

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriends
import com.codenode.budgetlens.friends.FriendsSelectRecyclerViewAdapter
import com.codenode.budgetlens.receipts.splitReceipt.SplitReceiptTotalPageActivity
import kotlinx.android.synthetic.main.activity_friends_page.*

class ReceiptSplitFriendSelect : AppCompatActivity() {

    private val selectedList: ArrayList<Int> = ArrayList()
    private lateinit var friendList: MutableList<Friends>
    private var friendsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendAdapter: RecyclerView.Adapter<FriendsSelectRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_split_friend_select)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val handleSplitByTotal: Button = findViewById(R.id.split_by_total)
        val handleSplitByItem: Button = findViewById(R.id.split_by_item)
        val additionalData = ""
        val receiptTotalValue = intent.getDoubleExtra("receipt total",0.0)
        val receiptId = intent.getIntExtra("receiptID",0)

        //Load Friend List
        friendList = UserFriends.loadFriendsFromAPI(this, pageSize, additionalData)

        val context = this
        friendsListRecyclerView = findViewById(R.id.friends_list)
        progressBar.visibility = View.VISIBLE

        if (friendList.isEmpty()) {
            friendsListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        if (friendsListRecyclerView != null) {
            friendsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            friendsListRecyclerView!!.layoutManager = linearLayoutManager
            friendAdapter = FriendsSelectRecyclerViewAdapter(friendList, selectedList)

            friendsListRecyclerView!!.adapter = friendAdapter
            progressBar.visibility = View.GONE
            friendsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        friendList =
                            UserFriends.loadFriendsFromAPI(context, pageSize, additionalData)
                        friendAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })

            handleSplitByTotal.setOnClickListener{
                // ToDo: SET UP GO TO NEXT ACTIVITY AND PASS IN SELECTED LIST AS EXTRA
                Log.i("Click", "Show $selectedList")
                val intent = Intent(this, SplitReceiptTotalPageActivity::class.java)
                intent.putIntegerArrayListExtra("itemId", selectedList)
                intent.putExtra("receipt total",receiptTotalValue)
                intent.putExtra("receiptID", receiptId)
                startActivity(intent)
            }

            handleSplitByItem.setOnClickListener{
                // ToDo: SET UP GO TO NEXT ACTIVITY AND PASS IN SELECTED LIST AS EXTRA
                val ids = intent.getIntExtra("ids", -1)
                val intent = Intent(this,  SplitItemListActivity::class.java)
                intent.putExtra("ids", ids)
                intent.putExtra("selectedList", ArrayList(selectedList))
                startActivityForResult(intent, 100)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==101){
            friendList =
                UserFriends.loadFriendsFromAPI(this, pageSize, "")
            friendAdapter.notifyDataSetChanged()
        }
    }
}