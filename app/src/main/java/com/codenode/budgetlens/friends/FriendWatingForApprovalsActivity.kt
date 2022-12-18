package com.codenode.budgetlens.friends

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.*
import com.google.android.material.switchmaterial.SwitchMaterial


class FriendWatingForApprovalsActivity : AppCompatActivity() {
    private lateinit var friendRSList: MutableList<FriendRequestSend>
    private var friendRSListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendRSAdapter: RecyclerView.Adapter<FriendRequestSendRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    //Todo: For the toggle button
   // private lateinit var friendRequestSwitch:SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request_send_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        var additionalData = ""
        //Load Friend List
        friendRSList =
            UserFriendRequestSend.loadFriendRequestSendFromAPI(this, pageSize, additionalData)
        val context = this
        friendRSListRecyclerView = findViewById(R.id.friend_request_send_list)
        progressBar.visibility = View.VISIBLE

        if (friendRSList.isEmpty()) {
            friendRSListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        if (friendRSListRecyclerView != null) {
            friendRSListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            friendRSListRecyclerView!!.layoutManager = linearLayoutManager
            friendRSAdapter = FriendRequestSendRecyclerViewAdapter(friendRSList)

            friendRSListRecyclerView!!.adapter = friendRSAdapter
            progressBar.visibility = View.GONE
            friendRSListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        friendRSList = UserFriendRequestSend.loadFriendRequestSendFromAPI(
                            context,
                            pageSize,
                            additionalData
                        )
                        friendRSAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })
        }



    }

}