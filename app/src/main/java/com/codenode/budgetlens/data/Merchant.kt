package com.codenode.budgetlens.data

import java.io.Serializable

class Merchant : Serializable {

    private var merchants: List<MerchantsBean?>? = null

    fun getMerchants(): List<MerchantsBean?>? {
        return merchants
    }

    fun setMerchants(merchants: List<MerchantsBean?>?) {
        this.merchants = merchants
    }

    class MerchantsBean : Serializable {
        /**
         * name : Apple
         */
        var name: String? = null
    }
}