package com.codenode.budgetlens.itemSplit

import android.app.Application
import com.codenode.budgetlens.data.ReceiptSplitItem

class ItemSplitListApp: Application() {
    var itemList: MutableList<ReceiptSplitItem>? = null
}