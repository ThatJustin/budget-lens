package com.codenode.budgetlens.adapter

import android.app.Application
import com.codenode.budgetlens.data.ReceiptSplitItem

class ItemSplitListApp: Application() {
    var itemList: MutableList<ReceiptSplitItem>? = null
}