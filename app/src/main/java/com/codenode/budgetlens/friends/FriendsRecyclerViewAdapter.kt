package com.codenode.budgetlens.friends
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.databinding.FriendsCardBinding
import com.codenode.budgetlens.databinding.FriendsCardPendingRequestBinding
import com.codenode.budgetlens.databinding.FriendsCardWaitingApprovalBinding

class FriendsRecyclerViewAdapter(private val friends: MutableList<Friends>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var context: Context? = null
    private val typeFriendCard = 0
    private val typeFriendPendingRequest = 1
    private val typeFriendWaitingForApproval = 2
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) :RecyclerView.ViewHolder{
        val view =
            FriendsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        /*  when (viewType) {
             typeFriendCard -> {
                 view =
                     LayoutInflater.from(parent.context).inflate(R.layout.friends_card, parent, false)
                 return FriendsCardViewHolder(view)
             }
             typeFriendPendingRequest -> {
                 view =
                     LayoutInflater.from(parent.context).inflate(R.layout.friends_card_pending_request, parent, false)
                 return FriendPendingRequestViewHolder(view)
             }
             typeFriendWaitingForApproval -> {
                 view =
                     LayoutInflater.from(parent.context).inflate(R.layout.friends_card_waiting_approval, parent, false)
                 return FriendWaitingForApprovalViewHolder(view)
             }
         }
         return null*/
        return    FriendsCardViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,position: Int) {
/*        if (holder is friendCardView) {
            val dataItem: String = getItem(position)*//*
            val friend = friends[position]
            holder.friendFirstName.text =
                holder.itemView.context.getString(R.string.friend_first_name, friend.firstName)
            holder.friendLastName.text=
                holder.itemView.context.getString(R.string.friend_last_name, friend.lastName)
            holder.friendInitial.text=
                holder.itemView.context.getString(R.string.friend_initial,friend.friendInitial)
     *//*   } else if (holder is VHHeaderWinner) {
            //cast holder to VHHeaderWinner and set data for header.
        } else if (holder is VHHeaderLooser) {
            //cast holder to VHHeaderLooser and set data for header.
        }*/

    }
    override fun getItemViewType(position: Int): Int {
        return if (friends[position].requestAccepted) typeFriendCard else typeFriendPendingRequest
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun getItemCount(): Int {
        return friends.size
    }
    inner class FriendsCardViewHolder(private val friendsCard: FriendsCardBinding) :
        RecyclerView.ViewHolder(friendsCard.root) {
        fun bind(friends: Friends){
            friendsCard.friendFirstName.text = friends.firstName
            friendsCard.friendLastName.text = friends.firstName
            friendsCard.friendInitial.text = friends.friendInitial
        }
    }
    inner class FriendPendingRequestViewHolder(private val friendPendingRequest: FriendsCardPendingRequestBinding ) :
        RecyclerView.ViewHolder(friendPendingRequest.root) {
        fun bind(friends: Friends){
            friendPendingRequest.friendFirstName.text = friends.firstName
            friendPendingRequest.friendLastName.text = friends.firstName
            friendPendingRequest.friendInitial.text = friends.friendInitial
        }

    inner class FriendWaitingForApprovalViewHolder(private val friendWaitingForApproval: FriendsCardWaitingApprovalBinding ) :
        RecyclerView.ViewHolder(friendWaitingForApproval.root) {
        fun bind(friends: Friends){
            friendWaitingForApproval.friendFirstName.text = friends.firstName
            friendWaitingForApproval.friendLastName.text = friends.firstName
            friendWaitingForApproval.friendInitial.text = friends.friendInitial

        }


    }




    }

}
