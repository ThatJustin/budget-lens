package com.codenode.budgetlens.receipts.filter

data class ReceiptsFilterOptions(
    var merchantName: String = "",
    var merchantId: Int = -1,
    var location: String = "",
    var locationId: Int = -1,
    var currency: String = "",
    var currencyId: Int = -1,
    var coupon: String = "",
    var couponId: Int = -1,
    var startDate: Long = 0,
    var endDate: Long = 0,
    var tax: Double = 0.00,
    var tip: Double = 00.00,
    var minPrice: Double = 00.00,
    var maxPrice: Double = 00.00
)