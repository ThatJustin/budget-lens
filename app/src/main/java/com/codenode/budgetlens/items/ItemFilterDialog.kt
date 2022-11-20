package com.codenode.budgetlens.items

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import com.codenode.budgetlens.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView


class ItemFilterDialog(context: Context, themeID: Int) : Dialog(context, themeID) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)


        val dialogView: View = layoutInflater.inflate(R.layout.item_filter_dialog, null)
        setContentView(dialogView)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );
        val closeDialog = findViewById<ImageButton>(R.id.filter_item_dialog_close)
        closeDialog.setOnClickListener {
            this.dismiss()
        }

        val merchantOptions: AutoCompleteTextView = findViewById(R.id.item_filter_merchant_options)
        val items = listOf(
            "",
            "A",
            "b",
            "H",
            "C",
            "B",
            "1",
            "hhh"
        ).sortedBy { it.lowercase() }
        val adapter = ArrayAdapter(context, R.layout.list_item, items)
        merchantOptions.setAdapter(adapter)
    }
}

