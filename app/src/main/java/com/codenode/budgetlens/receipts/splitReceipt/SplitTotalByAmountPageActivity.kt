package com.codenode.budgetlens.receipts.splitReceipt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import com.codenode.budgetlens.friends.ReceiptTotalParticipantRecyclerViewAdapter
import com.codenode.budgetlens.receipts.ReceiptInfoDialog
import com.codenode.budgetlens.receipts.ReceiptSplitFriendSelect
import com.google.android.material.button.MaterialButtonToggleGroup

class SplitTotalByAmountPageActivity : AppCompatActivity() {
    private var participantList = mutableListOf<Friends>()
    private lateinit var friendsList: MutableList<Friends>

    private var participantListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var participantAdapter: RecyclerView.Adapter<ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    private lateinit var emailInput: EditText
    private lateinit var textViewSplitTotalLeftValue: TextView
    private lateinit var confirmButton:Button
    private lateinit var cancelButton: Button
    var splitAmountArray: MutableList<Double> = ArrayList()
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_total_by_amount)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.splitTotalToggleButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        var additionalData = ""
        val participantIdArray = intent.getIntegerArrayListExtra("itemId")
        val receiptTotalValue = intent.getDoubleExtra("receipt total",0.0)
        cancelButton = findViewById(R.id.cancel_split_button)
        confirmButton = findViewById(R.id.confirm_split_button)

        Log.i("ReadSelectedList","The selected list is "+ participantIdArray.toString())
        Log.i("ReceiptTotal Passed", "The receipt total value was $receiptTotalValue")

        textViewSplitTotalLeftValue = findViewById(R.id.percentage_left)
        textViewSplitTotalLeftValue.text = receiptTotalValue.toString()
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
            participantAdapter = ReceiptTotalParticipantRecyclerViewAdapter(participantList,splitAmountArray)

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
            cancelButton.setOnClickListener{
                Log.i("Click", "Cancel Split Action ")
                val intent = Intent(this, ReceiptSplitFriendSelect::class.java)
                startActivity(intent)
            }
            confirmButton.setOnClickListener{
                splitAmountArray = splitAmountArray.subList(0,participantList.size)
                Log.i("Split Amount Array", "Split Amount Array is $splitAmountArray")
                Log.i("Participants List", "Participant List is $participantIdArray")

            }
        }



    }
}