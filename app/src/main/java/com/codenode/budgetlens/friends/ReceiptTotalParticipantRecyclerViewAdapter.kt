package com.codenode.budgetlens.friends

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends

class ReceiptTotalParticipantRecyclerViewAdapter(private val friends: MutableList<Friends>) :
    RecyclerView.Adapter<ReceiptTotalParticipantRecyclerViewAdapter.ViewHolder>() {
    val participantsIdList: MutableList<Int> = ArrayList()
    val splitAmount: MutableList<Double> = ArrayList()
    var isOnTextChanged: Boolean = false
    var amountLeft: Double = 0.0
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
        val friendSplitValue: EditText = friendsView.findViewById(R.id.split_value)

        init {
            friendsView.setOnClickListener(this)
            friendSplitValue.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(s: Editable) {

                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    isOnTextChanged = true
                }
            })
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