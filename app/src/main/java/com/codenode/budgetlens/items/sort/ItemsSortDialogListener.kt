package com.codenode.budgetlens.items.sort

interface ItemsSortDialogListener {
    fun onReturnedSortOptions(
        isPriceAscending: Boolean,
        isPriceDescending: Boolean,
        isNameAscending: Boolean,
        isNameDescending: Boolean
    )
}