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
    RecyclerView.Adapter<ParticipantsSelectRecyclerViewAdapter.ViewHolder>() {
    //    val selectedList: MutableList<Int> = ArrayList()
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_select_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        val firstNameShow: String = if (friend.firstName!!.length > 7) {
            friend.firstName!!.subSequence(0, 4).toString() + ".."
        } else
            friend.firstName!!
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, firstNameShow)
        val lastNameShow: String =
            if (friend.firstName!!.length <= 5 && friend.lastName!!.length > 8) {
                friend.lastName!!.subSequence(0, 3).toString() + ".."
            } else if (friend.lastName!!.length > 5 && friend.lastName!!.length > 8) {
                friend.lastName!!.subSequence(0, 2).toString() + ".."
            } else
                friend.lastName!!
        holder.friendLastName.text =
            holder.itemView.context.getString(R.string.friend_last_name, lastNameShow)
        holder.friendInitial.text =
            holder.itemView.context.getString(R.string.friend_initial, friend.friendInitial)
        holder.friendEmail.text =
            holder.itemView.context.getString(R.string.friend_email, friend.email)

        // Check if friend is in the splitList for the selected item
        val selectedItem = itemList.find { it.item_id == selected_item_id }
        if (selectedItem != null && selectedItem.splitList?.contains(friend.userId) == true) {
            holder.selectFriend.isChecked = true
        } else {
            holder.selectFriend.isChecked = false
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class ViewHolder(friendsView: View) : RecyclerView.ViewHolder(friendsView),
        View.OnClickListener {
        val friendEmail: TextView = friendsView.findViewById(R.id.friend_email)
        val friendFirstName: TextView = friendsView.findViewById(R.id.friend_first_name)
        val friendLastName: TextView = friendsView.findViewById(R.id.friend_last_name)
        val friendInitial: TextView = friendsView.findViewById(R.id.friend_initial)
        val selectFriend: CheckBox = friendsView.findViewById(R.id.friend_select)

        init {
            friendsView.setOnClickListener(this)
            selectFriend.setOnCheckedChangeListener { friendsView, isChecked ->
                val friendPosition = adapterPosition
                val itemPosition = itemList.indexOfFirst { it.item_id == selected_item_id }
                if (itemPosition >= 0) {
                    val item = itemList[itemPosition]
                    /*
                    ADD FRIEND ID TO LIST IF CHECKED, ELSE REMOVE FROM LIST.
                     */
                    if (isChecked) {
                        Log.i("Click", "Show " + friends[position].userId)
                        item.splitList = item.splitList!!.plus(friends[friendPosition].userId!!)
                    } else {
                        Log.i("uncheck ", "Show " + friends[position].userId)
                        item.splitList =
                            item.splitList!!.filter { it != friends[friendPosition].userId }
                    }
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val friend = friends[position]
                println("Clicked $friend")
            }


        }
    }
}