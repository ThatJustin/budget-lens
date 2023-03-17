package com.codenode.budgetlens.data

data class Items(
    var id: Int,
    var name: String,
    var price: Double,
    var scan_dates: String,
    var category_name: String
) {
}