package com.codenode.budgetlens.friends

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriendRequestReceive.Companion.loadFriendRequestReceiveFromAPI
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.switchmaterial.SwitchMaterial


class FriendPendingRequestsPageActivity : AppCompatActivity() {
    private lateinit var friendRRList: MutableList<Friends>
    private var friendRRListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendRRAdapter: RecyclerView.Adapter<FriendRequestReceiveRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request_receive_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.toggleButton)
        toggleButton.addOnButtonCheckedListener { toggleButton,checkedId, isChecked->
            val context = this
            val activity: Activity = context as Activity

            if(isChecked){
                when(checkedId){
                    R.id.show_friend_list -> {
                        val intent = Intent(context,FriendsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                    R.id.show_friend_request_send_list -> {
                        val intent = Intent(context,FriendWaitingForApprovalsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                }
            }
        }
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