package com.codenode.budgetlens.items.sort

interface ItemSortDialogListener {
    fun onReturnedSortOptions(
        isPriceAscending: Boolean,
        isPriceDescending: Boolean,
        isNameAscending: Boolean,
        isNameDescending: Boolean
    )
}