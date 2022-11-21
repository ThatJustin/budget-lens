package com.codenode.budgetlens.data

data class Friends (
    var userId: Int,
    var friendName: String,
    var trade_relation: String,
    var trade_amount: Double
    )
{
    constructor(): this(0,"","",0.0)
}