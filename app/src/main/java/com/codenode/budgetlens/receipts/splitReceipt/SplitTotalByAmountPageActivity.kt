package com.codenode.budgetlens.receipts.splitReceipt

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import com.google.android.material.button.MaterialButtonToggleGroup

class SplitTotalByAmountPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_total_by_amount)
        val toggleButton: MaterialButtonToggleGroup = findViewById(R.id.splitTotalToggleButton)


    }
}