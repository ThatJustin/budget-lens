package com.codenode.budgetlens.category

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Category
import com.codenode.budgetlens.data.UserCategories

class AddSubCategoryPopUpActivity : AppCompatActivity() {
    private lateinit var categoryList: MutableList<Category>
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sub_category_pop_up)

        // Make sure the user category list is empty
        UserCategories.userCategories.clear()
        categoryList = UserCategories.loadCategoriesFromAPI(this)
        val spinner = findViewById<Spinner>(R.id.chooseCategory)
        val adapter = CategoryDropdownAdapter(this, categoryList)
        spinner.adapter = adapter
        var parentCategory = spinner.selectedItem as Category
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                parentCategory = parent.getItemAtPosition(position) as Category
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        val categoryText = findViewById<EditText>(R.id.categoryNameText)
        val icon = "ic_baseline_category_24"

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.85).toInt(), (height * 0.55).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val confirmCreateSubCategoryButton: Button = findViewById(R.id.confirm_button)
        val cancelCreateSubCategoryButton: Button = findViewById(R.id.cancel_button)

        confirmCreateSubCategoryButton.setOnClickListener {
            if(categoryText.text.toString()!=""){
                UserCategories.createNewSubCategoryFromAPI(this, parentCategory, categoryText, icon)
                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,"Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }

        cancelCreateSubCategoryButton.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }
    }
}
