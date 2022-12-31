package com.codenode.budgetlens.friends.requests
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.data.Friends
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class FriendRequestReceiveRecyclerViewAdapter(private val friendRequestReceive: MutableList<Friends>) :
    RecyclerView.Adapter<FriendRequestReceiveRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_card_pending_request, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendRequestReceiveItem = friendRequestReceive[position]
        val firstNameShow:String = if(friendRequestReceiveItem.firstName.length>7){
            friendRequestReceiveItem.firstName.subSequence(0,4).toString()+".."
        }else
            friendRequestReceiveItem.firstName
        holder.friendFirstName.text =
            holder.itemView.context.getString(R.string.friend_first_name, firstNameShow)
        val lastNameShow:String = if(friendRequestReceiveItem.firstName.length<=5 && friendRequestReceiveItem.lastName.length>4){
            friendRequestReceiveItem.lastName.subSequence(0,3).toString()+".."
        }else if(friendRequestReceiveItem.lastName.length>5 && friendRequestReceiveItem.lastName.length>4){
            friendRequestReceiveItem.lastName.subSequence(0,2).toString()+".."
        }else
            friendRequestReceiveItem.lastName
        holder.friendLastName.text=
            holder.itemView.context.getString(R.string.friend_last_name, lastNameShow)
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
                acceptFriendRequest(context!!,position)
                Log.i("Click", "Friend Request at "+ adapterPosition+ " has been clicked")

            }
            friendRejectRequest.setOnClickListener{
                val position = adapterPosition
                rejectFriendRequest(context!!,position)
                Log.i("Click", "Friend Request at "+ adapterPosition+ " has been clicked")

            }

        }

        override fun onClick(p0: View?) {
            Log.i("Click", "Friend Request at "+ adapterPosition+ " has been clicked")
        }
        }
    private fun acceptFriendRequest(context: Context, position: Int){
        var success = false
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friend/request/${friendRequestReceive[position].userId}/"
        val friendRequestEndPoint = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()
        var answer = '1'
        val body = ("{\r\n" +
                "    \"answer\": \"${answer}\"\r\n" +
                "}").trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .method("PUT",body)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
            .addHeader("Content-Type", "text/plain")
            .build()
        friendRequestEndPoint.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                success = false
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            success = true

                            Log.i("Successful", "friend request has been accepted")

                    } else {
                        Log.e("Error", "Something went wrong ${response.message} ${response.headers}"
                        )
                    }
                }
                    val activity = context as Activity
                    Snackbar.make(
                        activity.findViewById<BottomNavigationView>(R.id.bottom_navigation),
                        "Friend Request Accepted.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                removeFriendRequest(position)            }
            }
        })
    }
    private fun rejectFriendRequest(context: Context,position: Int){
        var success = false
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/friend/request/${friendRequestReceive[position].userId}/"
        val friendRequestEndPoint = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()
        var answer = '0'
        val body = ("{\r\n" +
                "    \"answer\": \"${answer}\"\r\n" +
                "}").trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .method("PUT",body)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
            .addHeader("Content-Type", "text/plain")
            .build()
        friendRequestEndPoint.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                success = false
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            success = true

                            Log.i("Successful", "friend request has been rejected")

                        } else {
                            Log.e("Error", "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    }
                    val activity = context as Activity
                    Snackbar.make(
                        activity.findViewById<BottomNavigationView>(R.id.bottom_navigation),
                        "Friend Request Rejected.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    removeFriendRequest(position)            }
            }
        })
    }

    private fun removeFriendRequest(position: Int) {

        friendRequestReceive.removeAt(position)
        notifyItemRemoved(position)
    }
    }




