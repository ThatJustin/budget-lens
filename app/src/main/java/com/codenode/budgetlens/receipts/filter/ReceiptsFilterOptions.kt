package com.codenode.budgetlens.receipts.filter

data class ReceiptsFilterOptions(
    var merchantName: String = "",
    var location: String = "",
    var currency: String = "",
    var coupon: String = "",
    var total: String = "",
    var scanDateStart: String = "",
    var scanDateEnd: String = "",
    var tax: Double = 0.00,
    var tip: Double = 00.00
)