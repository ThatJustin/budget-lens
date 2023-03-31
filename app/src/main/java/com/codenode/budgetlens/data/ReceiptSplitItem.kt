package com.codenode.budgetlens.data

import java.io.Serializable

class ReceiptSplitItem : Serializable {
    /**
     * item_id : 5
     * item_name : aaa
     * item_price : 120
     * splitList: [1,2]
     */
    var item_id: Int? = null
    var item_name: String? = null
    var item_price: String? = null
    var splitList: List<Int>? = null
    var sharedWithSelf: Boolean = true
}