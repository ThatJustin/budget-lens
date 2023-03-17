package com.codenode.budgetlens.utils

interface HttpResponseListener {
    fun onHttpSuccess(viewItemRequestType : Int, mutableList: MutableList<*>, totalPrice: Double = 0.00)
    fun onHttpError()
}