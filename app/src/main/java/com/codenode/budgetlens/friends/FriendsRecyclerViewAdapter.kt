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
        holder.friendName.text =
            holder.itemView.context.getString(R.string.friend_name, friend.friendName)
        holder.tradeRelation.text=
            holder.itemView.context.getString(R.string.trade_relation, friend.trade_relation)
        holder.tradeAmount.text=
            holder.itemView.context.getString(R.string.trade_amount, friend.trade_amount)
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun getItemCount(): Int {
        return friends.size
    }
    inner class ViewHolder(friendsView: View) : RecyclerView.ViewHolder(friendsView), View.OnClickListener {
        val friendName: TextView = friendsView.findViewById(R.id.friend_name)
        val tradeRelation: TextView = friendsView.findViewById(R.id.trade_relation)
        val tradeAmount: TextView = friendsView.findViewById(R.id.trade_amount)
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
