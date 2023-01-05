package com.codenode.budgetlens.category

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Category
import com.codenode.budgetlens.data.UserCategories

class EditCategoryPopUpActivity : AppCompatActivity() {
    lateinit var category: Category
    private lateinit var adapter: RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>
    private var categoryListRecyclerView: RecyclerView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_sub_category_pop_up)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.7).toInt(), (height * 0.3).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Create the Intent to go back to the category activity page
        val goToCategoryActivity = Intent(this, CategoryActivity::class.java)

        // Find the category to be edited in this activity
        val extras = intent.getStringExtra("category")
        Log.d("Debug extras", extras.toString())
        for (i in UserCategories.userCategories) {
            if (i.category_name == extras.toString()) {
                category = i
                break
            }
        }

        // Cancel button goes back to category activity
        val cancelButton = findViewById<Button>(R.id.edit_cancel_button)
        val newName = findViewById<EditText>(R.id.editCategoryNameText)

        cancelButton.setOnClickListener {
            startActivity(goToCategoryActivity)
        }

        // Confirm button goes back to category activity and edits the category name
        val confirmButton = findViewById<Button>(R.id.edit_confirm_button)
        confirmButton.setOnClickListener {
            UserCategories.editCategory(this, category, newName)
            startActivity(goToCategoryActivity)
        }
    }
}