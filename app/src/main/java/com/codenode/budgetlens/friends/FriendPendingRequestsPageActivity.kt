package com.codenode.budgetlens.friends

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.FriendRequestReceive
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriendRequestReceive.Companion.loadFriendRequestReceiveFromAPI
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import com.google.android.material.switchmaterial.SwitchMaterial


class FriendPendingRequestsPageActivity : AppCompatActivity() {
    private lateinit var friendRRList: MutableList<FriendRequestReceive>
    private var friendRRListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendRRAdapter: RecyclerView.Adapter<FriendRequestReceiveRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    private lateinit var friendRequestSwitch:SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request_receive_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        var additionalData = ""
        //Load Friend List
        friendRRList = loadFriendRequestReceiveFromAPI(this, pageSize, additionalData)
        val context = this
        friendRRListRecyclerView = findViewById(R.id.friend_request_receive_list)
        progressBar.visibility = View.VISIBLE

        if (friendRRList.isEmpty()) {
            friendRRListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        if (friendRRListRecyclerView != null) {
            friendRRListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            friendRRListRecyclerView!!.layoutManager = linearLayoutManager
            friendRRAdapter = FriendRequestReceiveRecyclerViewAdapter(friendRRList)

            friendRRListRecyclerView!!.adapter = friendRRAdapter
            progressBar.visibility = View.GONE
            friendRRListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        friendRRList = loadFriendRequestReceiveFromAPI(context, pageSize, additionalData)
                        friendRRAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })
        }



    }
}