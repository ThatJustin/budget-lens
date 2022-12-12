package com.codenode.budgetlens.data

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FriendRequest (
    var userId: Int,
    var firstName: String,
    var lastName: String,
    var email: String,
    var friendInitial: Char

        ){
    constructor(): this(0,"","","",' ')

}

