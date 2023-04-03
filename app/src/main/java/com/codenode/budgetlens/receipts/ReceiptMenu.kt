package com.codenode.budgetlens.receipts

import com.google.gson.Gson
import org.json.JSONObject

 class ReceiptMenu{
    val merchants: List<String> = listOf()
    val locations: List<String> =listOf()
    val currency: List<String> =listOf()
    val coupon: List<Double> =listOf()
    val total: List<Double> =listOf()

    fun toReceiptMenu(json: String?): ReceiptMenu{
       if(json == null)return ReceiptMenu()
       return Gson().fromJson(json, ReceiptMenu::class.java)
    }
    /*
    {"merchants":["Costco","Tims"],"locations":["montreal","boston"],
    "currency":["CAD"],"coupon":[0.0,2.0,1.0]}
     */
}
