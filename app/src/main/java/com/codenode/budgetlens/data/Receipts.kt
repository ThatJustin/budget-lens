package com.codenode.budgetlens.data

data class Receipts (
    var isLoaded: Boolean = false,
    var scan_date: String,
    var receipt_image: String
) {
    constructor() : this(false, "", "")
}