package com.codenode.budgetlens.friends

import android.annotation.SuppressLint
import android.widget.ProgressBar
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.R
import android.view.View
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents

class FriendsPageActivity : AppCompatActivity() {
    private lateinit var friendList: MutableList<Friends>
    private var friendsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendAdapter: RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)

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