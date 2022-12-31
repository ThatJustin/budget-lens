package com.codenode.budgetlens.friends.requests
import com.codenode.budgetlens.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.codenode.budgetlens.data.Friends

class FriendRequestSendRecyclerViewAdapter(private val friendRequestSend: MutableList<Friends>) :
    RecyclerView.Adapter<FriendRequestSendRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_card_waiting_approval, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendRequestSendItem = friendRequestSend[position]
        val firstNameShow:String = if(friendRequestSendItem.firstName.length>7){
            friendRequestSendItem.firstName.subSequence(0,4).toString()+".."
        }else
            friendRequestSendItem.firstName
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, firstNameShow)
        val lastNameShow:String = if(friendRequestSendItem.firstName.length<=5 && friendRequestSendItem.lastName.length>4){
            friendRequestSendItem.lastName.subSequence(0,3).toString()+".."
        }else if(friendRequestSendItem.lastName.length>5 && friendRequestSendItem.lastName.length>4){
            friendRequestSendItem.lastName.subSequence(0,2).toString()+".."
        }else
            friendRequestSendItem.lastName
        holder.friendLastName.text=
            holder.itemView.context.getString(R.string.friend_last_name, lastNameShow)
        holder.friendInitial.text=
            holder.itemView.context.getString(R.string.friend_initial,friendRequestSendItem.friendInitial)
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun getItemCount(): Int {
        return friendRequestSend.size
    }
    inner class ViewHolder(friendRequestSendView: View) : RecyclerView.ViewHolder(friendRequestSendView) {
        val friendFirstName: TextView = friendRequestSendView.findViewById(R.id.friend_first_name)
        val friendLastName: TextView = friendRequestSendView.findViewById(R.id.friend_last_name)
        val friendInitial: TextView = friendRequestSendView.findViewById(R.id.friend_initial)


        }
    }



