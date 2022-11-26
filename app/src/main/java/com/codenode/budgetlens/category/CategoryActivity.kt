package com.codenode.budgetlens.category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Category
import com.codenode.budgetlens.data.UserCategories
import com.codenode.budgetlens.category.CategoryRecyclerViewAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class CategoryActivity : AppCompatActivity() {
    // Update the UI Thread for this page
//    fun createCategoryTabs(responseBody: String) {
//        findViewById<TextView>(R.id.category_text).text = responseBody
//    }
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

//        val url =
//            "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"
//
//        val registrationPost = OkHttpClient()
//
//        val request = Request.Builder()
//            .url(url)
//            .method("GET", body = null)
//            .addHeader("Content-Type", "application/json")
//            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
//            .build()
//
//        registrationPost.newCall(request).enqueue(object : Callback {
//
//            override fun onFailure(call: Call, e: java.io.IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.i("Response", "Got the response from server")
//                response.use {
//                    val responseBody = response.body?.string().toString()
//                    if (response.isSuccessful) {
//                        runOnUiThread {
//                            createCategoryTabs(responseBody)
//                        }
//
//                        Log.i("Successful", "Displayed Category string list successfully${responseBody}")
//                    } else {
//                        Log.e(
//                            "Error",
//                            "Something went wrong${responseBody} ${response.message} ${response.headers}"
//                        )
//                    }
//                }
//            }
//        })
        }
    }
}