package com.codenode.budgetlens.data

data class AddRece (
    var merchant: Int,
    var scan_date: String,
    var location: String,
    var total: String,
    var tax: String,
    var tip: String,
    var coupon: String,
    var currency: String,
   var receipt_text: String
    )
{
    constructor(): this(0,"","","","","","","","")
}