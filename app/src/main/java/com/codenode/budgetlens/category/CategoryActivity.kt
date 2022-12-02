package com.codenode.budgetlens.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
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
        CommonComponents.handleNavigationBar(ActivityName.CATEGORY_SETTINGS, this, this.window.decorView)

        UserCategories.userCategories.clear()

        categoryList = UserCategories.loadCategoriesFromAPI(this)

        Log.d("Debug List", categoryList.toString())

        categoryListRecyclerView = findViewById(R.id.categoryRecyclerView)

        if (categoryListRecyclerView != null) {
            Log.i("INfo", "something")
            categoryListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            categoryListRecyclerView!!.layoutManager = linearLayoutManager
            adapter = CategoryRecyclerViewAdapter(categoryList)
            categoryListRecyclerView!!.adapter = adapter
            categoryListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        categoryList =
                            UserCategories.loadCategoriesFromAPI(context)
                        adapter.notifyDataSetChanged()
                    }
                }
            })

//            Functionality for add subcategory popup

            var gotToAddAubCategoryPopUp = Intent(this, AddSubCategoryPopUpActivity::class.java)

            var addSubCategoryButton: ImageButton = findViewById(R.id.addSubcategoryButton)
            addSubCategoryButton.setOnClickListener{
                startActivity(gotToAddAubCategoryPopUp)
            }
        }
    }
}