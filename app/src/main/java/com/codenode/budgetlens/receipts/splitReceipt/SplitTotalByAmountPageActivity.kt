package com.codenode.budgetlens.receipts.splitReceipt

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import com.codenode.budgetlens.data.UserFriends.Companion.userFriends
import com.codenode.budgetlens.friends.FriendsRecyclerViewAdapter
import com.codenode.budgetlens.friends.ReceiptTotalParticipantRecyclerViewAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlin.math.log

class SplitTotalByAmountPageActivity : AppCompatActivity() {
    private var participantList = mutableListOf<Friends>()
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
        Log.i("ReadSelectedList","The selected list is "+ participantIdArray.toString())
        friendsList = loadFriendsFromAPI(this, pageSize, additionalData)
            if (participantIdArray != null) {
                for (item in participantIdArray) {
                    for (Friend in friendsList) {
                        if(Friend.userId == item){
                            participantList.add(Friend)
                            Log.i("addParticipant","Friend "+Friend.userId+" has been added to the participants list")
                        }
                    }
                }
            }else{
                Log.i("NonSelectedParticipants","There is no participants been selected")
            }


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