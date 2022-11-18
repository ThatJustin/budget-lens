package com.codenode.budgetlens

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.chip.Chip


class ItemSortDialog(context: Context, themeID: Int) : Dialog(context, themeID) {
    //Sort option false - ascending, true - descending
    var sortOption1 = false;
    var sortOption2 = false;
    var sortOption3 = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView: View = layoutInflater.inflate(R.layout.item_sort_dialog, null)
        setContentView(dialogView)

        // Set the width and height to the min values
        val itemSortDialog = findViewById<LinearLayout>(R.id.itemSortDialog)
        window!!.setLayout(itemSortDialog.minimumWidth, itemSortDialog.minimumHeight)

        //Handle saving
        val itemSortOptionsBtn = findViewById<Button>(R.id.save_item_sort_options)
        itemSortOptionsBtn.setOnClickListener {
            val itemSortOption1 = findViewById<Chip>(R.id.chip_1)
            val itemSortOption2 = findViewById<Chip>(R.id.chip_2)
            val itemSortOption3 = findViewById<Chip>(R.id.chip_3)
            val itemSortOption4 = findViewById<Chip>(R.id.chip_4)
            val itemSortOption5 = findViewById<Chip>(R.id.chip_5)
            val itemSortOption6 = findViewById<Chip>(R.id.chip_6)

            println("${itemSortOption1.isChecked}")
            println("${itemSortOption2.isChecked}")
            println("${itemSortOption3.isChecked}")
            println("${itemSortOption4.isChecked}")
            println("${itemSortOption5.isChecked}")
            println("${itemSortOption6.isChecked}")
        }
    }
}

