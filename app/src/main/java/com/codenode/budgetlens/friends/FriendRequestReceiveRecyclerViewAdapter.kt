package com.codenode.budgetlens.friends
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.FriendRequestReceive
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


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
    inner class ViewHolder(friendRequestReceiveView: View) : RecyclerView.ViewHolder(friendRequestReceiveView),
    View.OnClickListener{
        val friendFirstName: TextView = friendRequestReceiveView.findViewById(R.id.friend_first_name)
        val friendLastName: TextView = friendRequestReceiveView.findViewById(R.id.friend_last_name)
        val friendInitial: TextView = friendRequestReceiveView.findViewById(R.id.friend_initial)
        val friendAcceptRequest: ImageView = friendRequestReceiveView.findViewById(R.id.accept_request)
        val friendRejectRequest: ImageView = friendRequestReceiveView.findViewById(R.id.reject_request)

        //TODO: Implement those two functions
        init {
            friendAcceptRequest.setOnClickListener{
                val position = adapterPosition
                friendRequestReceive[position].isConfirmed = true
                removeFriendRequest(position)
            }
            friendRejectRequest.setOnClickListener{
                val position = adapterPosition
                friendRequestReceive[position].isConfirmed = false
                removeFriendRequest(position)
            }

        }

        override fun onClick(p0: View?) {
            Log.i("Click", "Friend Request at "+ adapterPosition+ " has been clicked")
        }
        }
    private fun removeFriendRequest(position: Int) {
        val activity = context as Activity
        Snackbar.make(
            activity.findViewById<BottomNavigationView>(R.id.bottom_navigation),
            "Receipt deleted.",
            Snackbar.LENGTH_SHORT
        ).show()
        friendRequestReceive.removeAt(position)
        notifyItemRemoved(position)
    }
    }


