package com.codenode.budgetlens.data

class FriendRequest (
    var userId: Int,
    var firstName: String,
    var lastName: String,
    var email: String,
    var friendInitial: Char

        ){
    constructor(): this(0,"","","",' ')

}