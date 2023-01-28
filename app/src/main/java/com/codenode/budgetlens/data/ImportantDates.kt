package com.codenode.budgetlens.data

data class ImportantDates(
    var id: Int,
    var date: String,
    var description: String,
    var item: Int
) {
    constructor() : this(0, "", "", 0)
}