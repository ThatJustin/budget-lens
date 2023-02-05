package com.codenode.budgetlens.receipts.splitReceipt

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import com.codenode.budgetlens.friends.FriendsRecyclerViewAdapter
import com.codenode.budgetlens.friends.ReceiptTotalParticipantRecyclerViewAdapter
import com.google.android.material.button.MaterialButtonToggleGroup

class SplitTotalByAmountPageActivity : AppCompatActivity() {
    private lateinit var participantList: MutableList<Friends>
    private lateinit var friendsList: MutableList<Friends>

    private var participantListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var participantAdapter: RecyclerView.Adapter<ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    private lateinit var emailInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_total_by_amount)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.splitTotalToggleButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        var additionalData = ""
        val participantIdArray = intent.getIntegerArrayListExtra("itemId")

        friendsList = loadFriendsFromAPI(this, pageSize, additionalData)
        val participantsIdSet : Set<Int>
        if (participantIdArray != null) {
            participantsIdSet = participantIdArray.toSet()
            for (Friend in friendsList) {
                if (participantsIdSet.contains(Friend.userId)) {
                    participantList.add(Friend)
                }
            }
        }else print("No friends was selected as participants")

        val context = this
        participantListRecyclerView = findViewById(R.id.participants_list)
        progressBar.visibility = View.VISIBLE

        if (participantList.isEmpty()) {
            participantListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        if (participantListRecyclerView != null) {
            participantListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            participantListRecyclerView!!.layoutManager = linearLayoutManager
            participantAdapter = ReceiptTotalParticipantRecyclerViewAdapter(participantList)

            participantListRecyclerView!!.adapter = participantAdapter
            progressBar.visibility = View.GONE
            participantListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        participantList = loadFriendsFromAPI(context, pageSize, additionalData)
                        participantAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })
        }



    }
}