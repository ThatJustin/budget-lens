package com.codenode.budgetlens.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.ReceiptSplitItem

class ParticipantsSelectRecyclerViewAdapter(
    private val friends: MutableList<Friends>,
    private val itemList: MutableList<ReceiptSplitItem>,
    private val selected_item_id: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sharedWithSelf = 0
    private val friend = 1
    private var sharedWithSelfChecked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            sharedWithSelf -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.friend_select_card, parent, false)
                SharedWithSelfViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.friend_select_card, parent, false)
                FriendViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            sharedWithSelf -> {
                val sharedWithSelfHolder = holder as SharedWithSelfViewHolder
                sharedWithSelfHolder.sharedWithSelfCheckBox.isChecked = sharedWithSelfChecked
                // Check if friend is in the splitList for the selected item
                val selectedItem = itemList.find { it.item_id == selected_item_id }
                if (selectedItem != null && selectedItem.sharedWithSelf == true) {
                    sharedWithSelfHolder.sharedWithSelfCheckBox.isChecked = true
                } else {
                    sharedWithSelfHolder.sharedWithSelfCheckBox.isChecked = false
                }
            }
            friend -> {
                val friendHolder = holder as FriendViewHolder
                val currentFriend = friends[position - 1]
                val firstNameShow: String = if (currentFriend.firstName!!.length > 7) {
                    currentFriend.firstName!!.subSequence(0, 4).toString() + ".."
                } else
                    currentFriend.firstName!!
                friendHolder.friendFirstName.text =
                    friendHolder.itemView.context.getString(
                        R.string.friend_first_name,
                        firstNameShow
                    )
                val lastNameShow: String =
                    if (currentFriend.firstName!!.length <= 5 && currentFriend.lastName!!.length > 8) {
                        currentFriend.lastName!!.subSequence(0, 3).toString() + ".."
                    } else if (currentFriend.lastName!!.length > 5 && currentFriend.lastName!!.length > 8) {
                        currentFriend.lastName!!.subSequence(0, 2).toString() + ".."
                    } else
                        currentFriend.lastName!!
                friendHolder.friendLastName.text =
                    friendHolder.itemView.context.getString(R.string.friend_last_name, lastNameShow)
                friendHolder.friendInitial.text =
                    friendHolder.itemView.context.getString(
                        R.string.friend_initial,
                        currentFriend.friendInitial
                    )
                friendHolder.friendEmail.text =
                    friendHolder.itemView.context.getString(
                        R.string.friend_email,
                        currentFriend.email
                    )

                // Check if friend is in the splitList for the selected item
                val selectedItem = itemList.find { it.item_id == selected_item_id }
                if (selectedItem != null && selectedItem.splitList?.contains(currentFriend.userId) == true) {
                    friendHolder.selectFriend.isChecked = true
                } else {
                    friendHolder.selectFriend.isChecked = false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return friends.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == sharedWithSelf) {
            sharedWithSelf
        } else {
            friend
        }
    }

    inner class SharedWithSelfViewHolder(friendsView: View) : RecyclerView.ViewHolder(friendsView),
        View.OnClickListener {
        val friendFirstName: TextView = friendsView.findViewById(R.id.friend_first_name)
        val friendInitial: TextView = friendsView.findViewById(R.id.friend_initial)
        val friendLastName: TextView = friendsView.findViewById(R.id.friend_last_name)
        val friendEmail: TextView = friendsView.findViewById(R.id.friend_email)
        val sharedWithSelfCheckBox: CheckBox = friendsView.findViewById(R.id.friend_select)

        init {
            friendInitial.text = "M"
            friendFirstName.text = "Me"
            friendLastName.text = ""
            friendEmail.text = ""
            friendsView.setOnClickListener(this)
            sharedWithSelfCheckBox.setOnCheckedChangeListener { _, isChecked ->
                val itemPosition = itemList.indexOfFirst { it.item_id == selected_item_id }
                if (itemPosition >= 0) {
                    val item = itemList[itemPosition]
                    /*
                    ADD FRIEND ID TO LIST IF CHECKED, ELSE REMOVE FROM LIST.
                     */
                    if (isChecked) {
                        Log.i("Click", "Show " + "Me")
                        item.sharedWithSelf = true
                    } else {
                        Log.i("uncheck ", "Show " + "Me")
                        item.sharedWithSelf = false
                    }
                }
            }
        }

        override fun onClick(v: View?) {
            //nothing
        }
    }

    inner class FriendViewHolder(friendsView: View) : RecyclerView.ViewHolder(friendsView),
        View.OnClickListener {
        val friendEmail: TextView = friendsView.findViewById(R.id.friend_email)
        val friendFirstName: TextView = friendsView.findViewById(R.id.friend_first_name)
        val friendLastName: TextView = friendsView.findViewById(R.id.friend_last_name)
        val friendInitial: TextView = friendsView.findViewById(R.id.friend_initial)
        val selectFriend: CheckBox = friendsView.findViewById(R.id.friend_select)

        init {
            friendsView.setOnClickListener(this)
            selectFriend.setOnCheckedChangeListener { _, isChecked ->
                val friendPosition = adapterPosition - 1
                val itemPosition = itemList.indexOfFirst { it.item_id == selected_item_id }
                if (itemPosition >= 0) {
                    val item = itemList[itemPosition]
                    /*
                    ADD FRIEND ID TO LIST IF CHECKED, ELSE REMOVE FROM LIST.
                     */
                    if (isChecked) {
                        Log.i("Click", "Show " + friends[friendPosition].userId)
                        item.splitList = item.splitList!!.plus(friends[friendPosition].userId!!)
                    } else {
                        Log.i("uncheck ", "Show " + friends[friendPosition].userId)
                        item.splitList =
                            item.splitList!!.filter { it != friends[friendPosition].userId }
                    }
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val friend = friends[position - 1]
                println("Clicked $friend")
            }
        }
    }
}
