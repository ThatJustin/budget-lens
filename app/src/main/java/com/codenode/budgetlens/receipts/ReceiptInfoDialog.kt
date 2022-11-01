package com.codenode.budgetlens.receipts

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import com.codenode.budgetlens.R


class ReceiptInfoDialog(context: Context) : Dialog(context), OnClickListener {
    override fun onClick(v: View?) {
        print("test")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_receipt_info)
    }

}