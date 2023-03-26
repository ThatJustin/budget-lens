package com.codenode.budgetlens.data

data class Receipts(
    var id: Int,
    var merchant_name: String = "N/A",
    var scan_date: String?,
    var receipt_image: String?,
    var location: String = "N/A",
    var total_amount: Double = 0.00,
    var tax: Double = 0.00,
    var tip: Double = 0.00,
    var coupon: Double = 0.00,
    var currency: String = "N/A",
) {
    constructor() : this(0, "N/A", "", "", "N/A", 0.0, 0.0, 0.0, 0.00, "N/A")
}