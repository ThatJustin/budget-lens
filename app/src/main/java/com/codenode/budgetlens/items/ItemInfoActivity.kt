package com.codenode.budgetlens.items

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.codenode.budgetlens.R
import com.codenode.budgetlens.databinding.ActivityMainBinding

class ItemInfoActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_item_info)
        val categories = resources.getStringArray(R.array.category_place_holder)
        val arrayAdapter = ArrayAdapter(this, R.layout.category_dropdown_item, categories)
        autoCompleteTextView = findViewById(R.id.category_dropdown)
        autoCompleteTextView.setAdapter(arrayAdapter)


    }
}