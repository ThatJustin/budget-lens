package com.codenode.budgetlens.data

data class Receipts (
    var id: Int,
    var merchant_name: String,
    var scan_date: String,
    var receipt_image: String,
    var location: String,
    var total_amount: Double,
    var tax: Double,
    var tip: Double,
    var coupon: Int,
    var currency: String,
    var important_dates: String,
) {
    constructor() : this(0, "", "", "", "", 0.0, 0.0, 0.0, 0, "", "")
}