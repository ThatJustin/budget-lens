package com.codenode.budgetlens.data

data class AddRece (
    /*
        params["merchant"] = merchant
        params["scan_date"] = tx1.text.toString()
        //params["receipt_image"] = file!!
        params["location"] = tx3.text.toString()
        params["total"] = tx4.text.toString()
        params["tax"] = tx5.text.toString()
        params["tip"] = tx6.text.toString()
        params["coupon"] = tx7.text.toString()
        params["currency"] = tvCurrency.text.toString()
        params["receipt_text"] = txt1.text.toString()*/
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