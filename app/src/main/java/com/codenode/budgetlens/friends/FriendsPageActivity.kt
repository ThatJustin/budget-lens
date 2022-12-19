package com.codenode.budgetlens.friends

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import com.google.android.material.button.MaterialButtonToggleGroup
import android.content.Context

class FriendsPageActivity : AppCompatActivity() {
    private lateinit var friendList: MutableList<Friends>
    private var friendsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendAdapter: RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    //Todo: For the toggle button
    // private lateinit var friendRequestSwitch:SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_page)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.toggleButton)
        toggleButton.addOnButtonCheckedListener { toggleButton,checkedId, isChecked->
            val context : Context = this
            val activity: Activity = context as Activity

            if(isChecked){
                when(checkedId){
                    R.id.show_friend_request_send_list -> {
                        val intent = Intent(context,FriendWaitingForApprovalsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)

                    }
                    R.id.show_friend_request_receive_list -> {
                        val intent = Intent(context,FriendPendingRequestsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                }
            }else{
                if (toggleButton.checkedButtonId == View.NO_ID) {
                    Log.i("Message","Nothing Selected")
                }

            }
        }

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        var additionalData = ""
        //Load Friend List
        friendList = loadFriendsFromAPI(this, pageSize, additionalData)
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
            friendAdapter = FriendsRecyclerViewAdapter(friendList)

            friendsListRecyclerView!!.adapter = friendAdapter
            progressBar.visibility = View.GONE
            friendsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        friendList = loadFriendsFromAPI(context, pageSize, additionalData)
                        friendAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })
        }



    }
}