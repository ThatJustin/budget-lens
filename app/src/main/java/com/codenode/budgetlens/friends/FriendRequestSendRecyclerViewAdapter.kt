package com.codenode.budgetlens.friends
import com.codenode.budgetlens.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.data.Friends
import android.widget.TextView

class FriendRequestSendRecyclerViewAdapter(private val friends: MutableList<Friends>) :
    RecyclerView.Adapter<FriendRequestSendRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :FriendRequestSendRecyclerViewAdapter.ViewHolder{
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: FriendRequestSendRecyclerViewAdapter.ViewHolder, position: Int) {
        val friend = friends[position]
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, friend.firstName)
        holder.friendLastName.text=
            holder.itemView.context.getString(R.string.friend_last_name, friend.lastName)
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
        val friendFirstName: TextView = friendsView.findViewById(R.id.friend_first_name)
        val friendLastName: TextView = friendsView.findViewById(R.id.friend_last_name)
        val friendInitial: TextView = friendsView.findViewById(R.id.friend_initial)
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