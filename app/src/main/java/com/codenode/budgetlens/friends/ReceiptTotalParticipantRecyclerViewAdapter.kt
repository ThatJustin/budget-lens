package com.codenode.budgetlens.friends

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

class ReceiptTotalParticipantRecyclerViewAdapter(private val friends: MutableList<Friends>) :
    RecyclerView.Adapter<ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder>() {
    //val selectedList: MutableList<Int> = ArrayList()
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder{
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.split_total_participants_list_model, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder, position: Int) {
        val friend = friends[position]
        val firstNameShow:String = if(friend.firstName.length>7){
            friend.firstName.subSequence(0,4).toString()+".."
        }else
            friend.firstName
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, firstNameShow)
        val lastNameShow:String = if(friend.firstName.length<=5 && friend.lastName.length>8){
            friend.lastName.subSequence(0,3).toString()+".."
        }else if(friend.lastName.length>5 && friend.lastName.length>8){
            friend.lastName.subSequence(0,2).toString()+".."
        }else
            friend.lastName
        holder.friendLastName.text=
            holder.itemView.context.getString(R.string.friend_last_name, lastNameShow)
        holder.friendInitial.text=
            holder.itemView.context.getString(R.string.friend_initial,friend.friendInitial)
    /*    holder.friendSplitValue.text=
            holder.itemView.context.getString(R.string.split_value,friend.splitValue)*/
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun getItemCount(): Int {
        return friends.size
    }
    inner class ViewHolder(friendsView: View) : RecyclerView.ViewHolder(friendsView), View.OnClickListener {
        val friendFirstName: TextView = friendsView.findViewById(R.id.participants_first_name)
        val friendLastName: TextView = friendsView.findViewById(R.id.participants_last_name)
        val friendInitial: TextView = friendsView.findViewById(R.id.participants_initial)
        val friendSplitValue: TextView = friendsView.findViewById(R.id.split_value)
        init {
            friendsView.setOnClickListener(this)
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