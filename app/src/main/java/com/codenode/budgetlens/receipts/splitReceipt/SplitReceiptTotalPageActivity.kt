package com.codenode.budgetlens.receipts.splitReceipt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.UserFriends.Companion.loadFriendsFromAPI
import com.codenode.budgetlens.friends.ReceiptTotalParticipantRecyclerViewAdapter
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.*
import okhttp3.*

class SplitReceiptTotalPageActivity : AppCompatActivity() {
    private var participantList = mutableListOf<Friends>()
    private lateinit var friendsList: MutableList<Friends>
    private var participantListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var participantAdapter: RecyclerView.Adapter<ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder>? =
        null
    private var pageSize = 5
    private lateinit var textViewSplitTotalLeftValue: TextView
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private var receiptTotalAmountPassed: Double = 0.0
    var isPercentageChecked: Boolean = true
    var splitValueArray: MutableList<Double> = ArrayList()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_total_by_amount)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)
        val topBar: MaterialToolbar = findViewById(R.id.topAppBar)
        val bottomText: TextView = findViewById(R.id.total_items_cost_value)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        var additionalData = ""
        val participantIdArray = intent.getIntegerArrayListExtra("itemId")
        val receiptId = intent.getIntExtra("receiptID",0)
        receiptTotalAmountPassed = intent.getDoubleExtra("receipt total", 0.0)
        var receiptTotalValue = receiptTotalAmountPassed
        cancelButton = findViewById(R.id.cancel_split_button)
        confirmButton = findViewById(R.id.confirm_split_button)
        participantAdapter = ReceiptTotalParticipantRecyclerViewAdapter(
            participantList,
            splitValueArray,
            receiptTotalValue
        )

        Log.i("ReadSelectedList", "The selected list is " + participantIdArray.toString())
        Log.i("ReceiptTotal Passed", "The receipt total value was $receiptTotalValue")

        textViewSplitTotalLeftValue = findViewById(R.id.total_value_left)
        textViewSplitTotalLeftValue.text = receiptTotalValue.toString()

        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.splitTotalToggleButton)
        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            splitValueArray = ArrayList()
            if (isChecked) {
                when (checkedId) {
                    R.id.PercentageButton -> {
                        isPercentageChecked = true
                        topBar.title = "Split By Receipt Total - Percentage"
                        bottomText.text = "Percentage Left (%):"
                        receiptTotalValue = 100.0
                        textViewSplitTotalLeftValue.text = receiptTotalValue.toString()
                    }
                    R.id.AmountButton -> {
                        isPercentageChecked = false
                        topBar.title = "Split By Receipt Total - Amount"
                        bottomText.text = "Amount Left ($):"
                        receiptTotalValue = receiptTotalAmountPassed
                        textViewSplitTotalLeftValue.text = receiptTotalValue.toString()
                    }
                }
                participantAdapter = ReceiptTotalParticipantRecyclerViewAdapter(
                    participantList,
                    splitValueArray,
                    receiptTotalValue
                )
                participantListRecyclerView!!.adapter = participantAdapter
            } else {
                if (toggleButton.checkedButtonId == View.NO_ID) {
                    Log.i("Message", "Nothing Selected")
                }
            }
        }
        friendsList = loadFriendsFromAPI(this, pageSize, additionalData)
        if (participantIdArray != null) {
            for (item in participantIdArray) {
                for (Friend in friendsList) {
                    if (Friend.userId == item) {
                        participantList.add(Friend)
                        Log.i(
                            "addParticipant",
                            "Friend " + Friend.userId + " has been added to the participants list"
                        )
                    }
                }
            }
        } else {
            Log.i("NonSelectedParticipants", "There is no participants been selected")
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
//            participantAdapter = ReceiptTotalParticipantRecyclerViewAdapter(participantList,splitAmountArray,receiptTotalValue)
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
                        participantAdapter?.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE

                }
            })
            cancelButton.setOnClickListener {
                Log.i("Click", "Cancel Split Action ")
                val intent = Intent(this, ReceiptsListPageActivity::class.java)
                startActivity(intent)
            }
            confirmButton.setOnClickListener {
                splitValueArray = splitValueArray.subList(0, participantList.size)
                Log.i("Split Amount Array", "Split Amount Array is $splitValueArray")
                Log.i("Participants List", "Participant List is $participantIdArray")
    /*            val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                    .build()

                // for sending the participants list
                val participantsRequestBody = JSONObject().apply {
                    put("get_user_list", JSONArray(participantList))
                }
                val participantsBody = participantsRequestBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val participantsListRequest = Request.Builder()
                    .url("http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/itemsplit/sharedUsers/item_id=<$receiptId>/")
                    .post(participantsBody)
                    .build()

                // Send the first request
                val participantsListResponse = client.newCall(participantsListRequest).execute()

                // for sending the split value array list
                val requestBody2 = JSONObject().apply {
                    put("get_shared_amount", JSONArray(splitValueArray))
                }
                val splitValueBody = requestBody2.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val splitValueRequest = Request.Builder()
                    .url("http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/itemsplit/sharedAmount/item_id=<$receiptId>/")
                    .post(splitValueBody)
                    .build()

                // Send the second request
                val splitValueResponse = client.newCall(splitValueRequest).execute()
                if(participantsListResponse.isSuccessful&&splitValueResponse.isSuccessful){
                    val participantsListResponseBody = participantsListResponse.body?.string()
                    val splitValueResponseBody = splitValueResponse.body?.string()
                    if(participantsListResponseBody != null && splitValueResponseBody != null){
                        Toast.makeText(this,"The Split form has been submitted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ReceiptsListPageActivity::class.java)
                        startActivity(intent)
                    }else{
                        Log.i("Empty", "Something went wrong${participantsListResponse.body?.string()}")
                        Log.i("Empty", "Something went wrong${splitValueResponse.body?.string()}")
                    }

                }else{
                    Log.e(
                        "Error",
                        "Something went wrong${participantsListResponse.body?.string()} ${participantsListResponse.message} ${participantsListResponse.headers}"
                    )
                    Log.e(
                        "Error",
                        "Something went wrong${splitValueResponse.body?.string()} ${splitValueResponse.message} ${splitValueResponse.headers}"
                    )
                }*/


            }
        }


    }
}