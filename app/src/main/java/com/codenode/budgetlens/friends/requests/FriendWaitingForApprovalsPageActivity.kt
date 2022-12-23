package com.codenode.budgetlens.friends.requests

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.*
import com.codenode.budgetlens.friends.FriendsPageActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FriendWaitingForApprovalsPageActivity : AppCompatActivity() {
    private lateinit var friendRSList: MutableList<Friends>
    private var friendRSListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendRSAdapter: RecyclerView.Adapter<FriendRequestSendRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    private lateinit var emailInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request_send_list_page)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)
        val addFriendButton: Button = findViewById(R.id.add_button)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.toggleButton)
        toggleButton.addOnButtonCheckedListener { toggleButton,checkedId, isChecked->
            val context = this
            val activity: Activity = context as Activity

            if(isChecked){
                when(checkedId){
                    R.id.show_friend_request_receive_list -> {
                        val intent = Intent(context, FriendPendingRequestsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                    R.id.show_friend_list -> {
                        val intent = Intent(context, FriendsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                }
            }
        }
        fun validateEmail(): Boolean {
            if (emailInput.length() == 0) {
                emailInput.error = "This field is required"
                return false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                emailInput.error = "This field is not a valid email address"
                return false
            } else {
                emailInput.error = null
                return true
            }
        }

        emailInput = EditText(this)
        emailInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailInput.hint = "Email address"

        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }

            override fun afterTextChanged(s: Editable?) { /* dont care */
            }
        })

        addFriendButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Add Friend")
                .setMessage("Enter the email of the friend you want to add")
                .setView(emailInput)

                .setPositiveButton("Add") { dialog, which ->
                    println(this@FriendWaitingForApprovalsPageActivity)
                    if (validateEmail()) {
                        println("[validated] add : email -> " + emailInput.text.toString())
                        UserFriends.sendFriendRequest(this, emailInput)
                        //setContentView(R.layout.activity_friend_page)
                        val intent = Intent(this, FriendsPageActivity::class.java)
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Cancel") { dialog, which ->

                    dialog.dismiss()
                    //setContentView(R.layout.activity_friend_page)
                    val intent = Intent(this, FriendsPageActivity::class.java)
                    startActivity(intent)
                }
                .show()
        }

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