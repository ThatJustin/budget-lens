package com.codenode.budgetlens.friends
import com.codenode.budgetlens.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.codenode.budgetlens.data.FriendRequestReceive

class FriendRequestReceiveRecyclerViewAdapter(private val friendRequestReceive: MutableList<FriendRequestReceive>) :
    RecyclerView.Adapter<FriendRequestReceiveRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :FriendRequestReceiveRecyclerViewAdapter.ViewHolder{
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_card_waiting_approval, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: FriendRequestReceiveRecyclerViewAdapter.ViewHolder, position: Int) {
        val friendRequestReceiveItem = friendRequestReceive[position]
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, friendRequestReceiveItem.firstName)
        holder.friendLastName.text=
            holder.itemView.context.getString(R.string.friend_last_name, friendRequestReceiveItem.lastName)
        holder.friendInitial.text=
            holder.itemView.context.getString(R.string.friend_initial,friendRequestReceiveItem.friendInitial)
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun getItemCount(): Int {
        return friendRequestReceive.size
    }
    inner class ViewHolder(friendRequestReceiveView: View) : RecyclerView.ViewHolder(friendRequestReceiveView) {
        val friendFirstName: TextView = friendRequestReceiveView.findViewById(R.id.friend_first_name)
        val friendLastName: TextView = friendRequestReceiveView.findViewById(R.id.friend_last_name)
        val friendInitial: TextView = friendRequestReceiveView.findViewById(R.id.friend_initial)


        }
    }



