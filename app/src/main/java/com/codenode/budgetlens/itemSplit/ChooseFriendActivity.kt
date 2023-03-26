package com.codenode.budgetlens.itemSplit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.adapter.ParticipantsSelectRecyclerViewAdapter
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.ReceiptSplitItem
import com.codenode.budgetlens.data.UserFriends
import kotlinx.android.synthetic.main.activity_choose_friend.*

class ChooseFriendActivity : AppCompatActivity() {
    private var participants: MutableList<Friends> = ArrayList()
    var item_list = mutableListOf<ReceiptSplitItem>()
    private lateinit var friendList: MutableList<Friends>
    private lateinit var friendAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var friendsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var pageSize = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friend)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)
        friendList = UserFriends.loadFriendsFromAPI(this, pageSize, "")
        val selectedList = intent.getIntegerArrayListExtra("selectedList")
        val selected_item_id = intent.getIntExtra("selected_item_id", -1)
        participants = selectedList?.let { filterFriendsByUserId(friendList, it) } ?: mutableListOf()
        val app = application as ItemSplitListApp
        item_list = app.itemList ?: mutableListOf()
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
            friendAdapter =
                ParticipantsSelectRecyclerViewAdapter(participants, item_list, selected_item_id)

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
                            UserFriends.loadFriendsFromAPI(context, pageSize, "")
                        friendAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })

        }

        // find the Cancel button and add click listener
        val cancelButton = findViewById<TextView>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra("selectedList", selectedList?.let { ArrayList(it) })
            setResult(Activity.RESULT_OK, intent)
            finish() // close the activity and go back
        }

        // find the Confirm button and add click listener
        val confirmButton = findViewById<TextView>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            // create an intent to return to previous activity with selected participants list
            val intent = Intent()
            val app = application as ItemSplitListApp
            app.itemList = item_list
            intent.putExtra("selectedList", selectedList?.let { ArrayList(it) })
            setResult(Activity.RESULT_OK, intent)
            finish() // close the activity and return to previous activity
        }
    }

    private fun filterFriendsByUserId(friendList: MutableList<Friends>, selectedList: ArrayList<Int>): MutableList<Friends> {

        val participants = mutableListOf<Friends>()
        for (friend in friendList) {
            if (selectedList.contains(friend.userId)) {
                participants.add(friend)
            }
        }
        return participants

    }
}
