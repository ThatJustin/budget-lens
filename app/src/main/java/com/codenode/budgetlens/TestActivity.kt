package com.codenode.budgetlens

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val btn = findViewById<Button>(R.id.filter_item_btn_open)
        btn.setOnClickListener {
            val dialog = ItemFilterDialog(this)
            dialog.show();
        }
        val btn2 = findViewById<Button>(R.id.sort_item_btn_open)
        btn2.setOnClickListener {
            val dialog = ItemSortDialog(this, R.style.ItemSortDialog)
            dialog.show();
        }
    }
}