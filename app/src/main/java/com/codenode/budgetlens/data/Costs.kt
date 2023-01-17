package com.codenode.budgetlens.data

import java.io.Serializable

class Costs {
    var Costs: List<CostsBean>? = null

    class CostsBean : Serializable {
        /**
         * category_name : transportation
         * category_cost : 8.99
         */
        var category_name: String? = null
        var category_cost :Float?= null
        override fun toString(): String {
            return "CostsBean(category_name=$category_name, category_cost=$category_cost)"
        }

    }

    override fun toString(): String {
        return "Costs(Costs=$Costs)"
    }
}
