package com.codenode.budgetlens.utils

interface HttpResponseListener {
    fun onHttpSuccess(viewItemRequestType : Int, mutableList: MutableList<*>)
    fun onHttpError()
}