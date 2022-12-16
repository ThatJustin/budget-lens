package com.codenode.budgetlens.friends
import com.codenode.budgetlens.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.codenode.budgetlens.data.FriendRequestSend

class FriendRequestSendRecyclerViewAdapter(private val friendRequestSend: MutableList<FriendRequestSend>) :
    RecyclerView.Adapter<FriendRequestSendRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :FriendRequestSendRecyclerViewAdapter.ViewHolder{
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_card_pending_request, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: FriendRequestSendRecyclerViewAdapter.ViewHolder, position: Int) {
        val friendRequestSendItem = friendRequestSend[position]
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, friendRequestSendItem.firstName)
        holder.friendLastName.text=
            holder.itemView.context.getString(R.string.friend_last_name, friendRequestSendItem.lastName)
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



