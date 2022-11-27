package com.codenode.budgetlens.items.filter

data class ItemFilterOptions(
    var merchantName: String = "",
    var categoryName: String = "",
    var categoryId: Int = -1,
    var startDate: Long = 0,
    var endDate: Long = 0,
    var minPrice: Double = 00.00,
    var maxPrice: Double = 00.00
)
