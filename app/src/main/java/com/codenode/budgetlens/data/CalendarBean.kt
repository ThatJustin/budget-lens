package com.codenode.budgetlens.data

import java.io.Serializable

class CalendarBean  : Serializable {

    var data: List<DataDTO>? = null

    class DataDTO : Serializable {
        /**
         * item_id : 5
         * item_name : aaa
         * item_price : 120
         * splititem : [{"split_id":5,"orignal_user":"Emma","shared_user":"Sam"}]
         */
        var item_id: Int? = null
        var item_name: String? = null
        var item_price: String? = null
        var splititem: List<SplititemDTO>? = null

        class SplititemDTO : Serializable {
            /**
             * split_id : 5
             * orignal_user : Emma
             * shared_user : Sam
             */
            var split_id: Int? = null
            var orignal_user: String? = null
            var shared_user: String? = null
        }
    }
}