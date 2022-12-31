package com.codenode.budgetlens.items.filter

data class ItemsFilterOptions(
    var merchantName: String = "",
    var merchantId: Int = -1,
    var categoryName: String = "",
    var categoryId: Int = -1,
    var startDate: String = "",
    var endDate: String = "",
    var minPrice: Double = 00.00,
    var maxPrice: Double = 00.00
)
