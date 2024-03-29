package com.codenode.budgetlens.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Category
import com.codenode.budgetlens.data.UserCategories
import okhttp3.*


class CategoryActivity : AppCompatActivity() {
    private lateinit var categoryList: MutableList<Category>
    private var categoryListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Get the context from this object in order to get the token for accessing the API calls
        val context: Context = this

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(
            ActivityName.CATEGORY_SETTINGS,
            this,
            this.window.decorView
        )
        CommonComponents.handleScanningReceipts(this.window.decorView, this, ActivityName.CATEGORY_SETTINGS)
        // Make sure the user category list is empty
        UserCategories.userCategories.clear()
        categoryList = UserCategories.loadCategoriesFromAPI(this)

        categoryListRecyclerView = findViewById(R.id.categoryRecyclerView)
        if (categoryListRecyclerView != null) {
            categoryListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            categoryListRecyclerView!!.layoutManager = linearLayoutManager
            adapter = CategoryRecyclerViewAdapter(categoryList)
            categoryListRecyclerView!!.adapter = adapter

            // Functionality for add subcategory popup
            val gotToAddAubCategoryPopUp = Intent(this, AddSubCategoryPopUpActivity::class.java)
            val addSubCategoryButton: Button = findViewById(R.id.addSubcategoryButton)
            addSubCategoryButton.setOnClickListener {
                startActivity(gotToAddAubCategoryPopUp)
            }
        }
    }
}