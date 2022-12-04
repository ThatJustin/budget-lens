package com.codenode.budgetlens.items.sort

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import com.codenode.budgetlens.R
import com.codenode.budgetlens.items.ItemListActivity
import com.google.android.material.chip.Chip


class ItemSortDialog(context: Context, themeID: Int, var sortOptions: ItemListActivity.SortOptions) : Dialog(context, themeID) {
    private var itemSortDialogListener: ItemSortDialogListener? = null
    private var activityContext = context;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //set the listener
        itemSortDialogListener = activityContext as Activity as ItemSortDialogListener

        val dialogView: View = layoutInflater.inflate(R.layout.item_sort_dialog, null)
        setContentView(dialogView)

        // Set the width and height to the min values
        val itemSortDialog = findViewById<LinearLayout>(R.id.itemSortDialog)
        window!!.setLayout(itemSortDialog.minimumWidth, itemSortDialog.minimumHeight)

        val isPriceAscending = findViewById<Chip>(R.id.chip_1)
        val isPriceDescending = findViewById<Chip>(R.id.chip_2)
        val isNameAscending = findViewById<Chip>(R.id.chip_3)
        val isNameDescending = findViewById<Chip>(R.id.chip_4)

        //set the sort options that were previously selected
        isPriceAscending.isChecked = sortOptions.isPriceAscending
        isPriceDescending.isChecked = sortOptions.isPriceDescending
        isNameAscending.isChecked = sortOptions.isNameAscending
        isNameDescending.isChecked = sortOptions.isNameDescending

        //Handle saving
        val itemSortOptionsBtn = findViewById<Button>(R.id.save_item_sort_options)
        itemSortOptionsBtn.setOnClickListener {
            if (itemSortDialogListener != null) {
                itemSortDialogListener!!.onReturnedSortOptions(
                    isPriceAscending.isChecked,
                    isPriceDescending.isChecked,
                    isNameAscending.isChecked,
                    isNameDescending.isChecked
                )
            }
            this.dismiss()
        }
    }
}

