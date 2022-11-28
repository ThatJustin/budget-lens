package com.codenode.budgetlens.receipts.sort

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import com.codenode.budgetlens.R
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity
import com.google.android.material.chip.Chip

class ReceiptsSortDialog(context: Context, themeID: Int, var sortOptions: ReceiptsListPageActivity.SortOptions) : Dialog(context, themeID) {
    private var receiptsSortDialogListener: ReceiptsSortDialogListener? = null
    private var activityContext = context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        //set the listener
        receiptsSortDialogListener = activityContext as Activity as ReceiptsSortDialogListener

        val dialogView: View = layoutInflater.inflate(R.layout.receipts_sort_dialog, null)
        setContentView(dialogView)

        // Set the width and height to the min values
        val receiptsSortDialog = findViewById<LinearLayout>(R.id.receiptsSortDialog)
        window!!.setLayout(receiptsSortDialog.minimumWidth, receiptsSortDialog.minimumHeight)

        val isMerchantAscending = findViewById<Chip>(R.id.chip_1)
        val isMerchantDescending = findViewById<Chip>(R.id.chip_2)
        val isLocationAscending = findViewById<Chip>(R.id.chip_3)
        val isLocationDescending = findViewById<Chip>(R.id.chip_4)
        val isCouponAscending = findViewById<Chip>(R.id.chip_5)
        val isCouponDescending = findViewById<Chip>(R.id.chip_6)
        val isTaxAscending = findViewById<Chip>(R.id.chip_7)
        val isTaxDescending = findViewById<Chip>(R.id.chip_8)
        val isTipAscending = findViewById<Chip>(R.id.chip_9)
        val isTipDescending = findViewById<Chip>(R.id.chip_10)
        val isTotalAscending = findViewById<Chip>(R.id.chip_11)
        val isTotalDescending = findViewById<Chip>(R.id.chip_12)

        //set the sort options that were previously selected
        isMerchantAscending.isChecked = sortOptions.isMerchantAscending
        isMerchantDescending.isChecked = sortOptions.isMerchantDescending
        isLocationAscending.isChecked = sortOptions.isLocationAscending
        isLocationDescending.isChecked = sortOptions.isLocationDescending
        isCouponAscending.isChecked = sortOptions.isCouponAscending
        isCouponDescending.isChecked = sortOptions.isCouponDescending
        isTaxAscending.isChecked = sortOptions.isTaxAscending
        isTaxDescending.isChecked = sortOptions.isTaxDescending
        isTipAscending.isChecked = sortOptions.isTipAscending
        isTipDescending.isChecked = sortOptions.isTipDescending
        isTotalAscending.isChecked = sortOptions.isTotalAscending
        isTotalDescending.isChecked = sortOptions.isTotalDescending

        //Handle saving
        val receiptsSortOptionsBtn = findViewById<Button>(R.id.save_receipts_sort_options)
        receiptsSortOptionsBtn.setOnClickListener {
            if (receiptsSortDialogListener != null) {
                receiptsSortDialogListener!!.onReturnedSortOptions(
                    isMerchantAscending.isChecked,
                    isMerchantDescending.isChecked,
                    isLocationAscending.isChecked,
                    isLocationDescending.isChecked,
                    isCouponAscending.isChecked,
                    isCouponDescending.isChecked,
                    isTaxAscending.isChecked,
                    isTaxDescending.isChecked,
                    isTipAscending.isChecked,
                    isTipDescending.isChecked,
                    isTotalAscending.isChecked,
                    isTotalDescending.isChecked
                )
            }
            this.dismiss()
        }
    }
}