package com.codenode.budgetlens.data

data class ImportantDates(
    var id: Int,
    var date: String,
    var description: String,
    var user: Int,
    var item: Int
) {
    constructor() : this( 0, "", "",0,0)
}