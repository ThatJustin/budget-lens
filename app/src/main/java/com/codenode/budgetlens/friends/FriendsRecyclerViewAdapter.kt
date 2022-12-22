package com.codenode.budgetlens.friends
import com.codenode.budgetlens.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.data.Friends
import android.widget.TextView

class FriendsRecyclerViewAdapter(private val friends: MutableList<Friends>) :
    RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :FriendsRecyclerViewAdapter.ViewHolder{
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: FriendsRecyclerViewAdapter.ViewHolder,position: Int) {
        val friend = friends[position]
        val firstNameShow:String = if(friend.firstName.length>7){
            friend.firstName.subSequence(0,4).toString()+".."
        }else
            friend.firstName
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, firstNameShow)
        val lastNameShow:String = if(friend.firstName.length<=5 && friend.lastName.length>4){
            friend.lastName.subSequence(0,3).toString()+".."
        }else if(friend.lastName.length>5 && friend.lastName.length>4){
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