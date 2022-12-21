package com.codenode.budgetlens.receipts.sort

interface ReceiptsSortDialogListener {
    fun onReturnedSortOptions(
        isMerchantAscending: Boolean,
        isMerchantDescending: Boolean,
        isLocationAscending: Boolean,
        isLocationDescending: Boolean,
        isCouponAscending: Boolean,
        isCouponDescending: Boolean,
        isTaxAscending: Boolean,
        isTaxDescending: Boolean,
        isTipAscending: Boolean,
        isTipDescending: Boolean,
        isTotalAscending: Boolean,
        isTotalDescending: Boolean
    )
}