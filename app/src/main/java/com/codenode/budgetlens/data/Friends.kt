package com.codenode.budgetlens.data

data class Friends (
    var userId: Int?,
    var firstName: String?,
    var lastName: String?,
    var email: String,
    var friendInitial: Char?,

//    var trade_relation: String,
//    var trade_amount: Double
    )
{
    constructor(): this(0,"","","",' ')
}