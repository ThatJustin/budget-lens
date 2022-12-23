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
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import com.codenode.budgetlens.data.UserFriends.Companion.sendFriendRequest
import com.codenode.budgetlens.friends.requests.FriendPendingRequestsPageActivity
import com.codenode.budgetlens.friends.requests.FriendWaitingForApprovalsPageActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FriendsPageActivity : AppCompatActivity() {
    private lateinit var friendList: MutableList<Friends>
    private var friendsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendAdapter: RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    private lateinit var emailInput: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_page)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.FRIENDS, this, this.window.decorView)
        val addFriendButton: Button = findViewById(R.id.add_button)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.toggleButton)
        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            val context: Context = this
            val activity: Activity = context as Activity

            if (isChecked) {
                when (checkedId) {
                    R.id.show_friend_request_send_list -> {
                        val intent =
                            Intent(context, FriendWaitingForApprovalsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)

                    }
                    R.id.show_friend_request_receive_list -> {
                        val intent = Intent(context, FriendPendingRequestsPageActivity::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                }
            } else {
                if (toggleButton.checkedButtonId == View.NO_ID) {
                    Log.i("Message", "Nothing Selected")
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
                    println(this@FriendsPageActivity)
                    if (validateEmail()) {
                        println("[validated] add : email -> " + emailInput.text.toString())
                        sendFriendRequest(this, emailInput)
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

