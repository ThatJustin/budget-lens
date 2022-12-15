package com.codenode.budgetlens.data


class FriendRequestReceive (
    var userId: Int,
    var firstName: String,
    var lastName: String,
    var email: String,
    var friendInitial: Char,
    var isConfirmed: Boolean

        ){
    constructor(): this(0,"","","",' ',true)

}

