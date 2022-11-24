package com.codenode.budgetlens.data

data class Friends (
    var userId: Int,
    var lastName: String,
    var firstName: String,
    var email: String
//    var trade_relation: String,
//    var trade_amount: Double
    )
{
    constructor(): this(0,"","","")
}