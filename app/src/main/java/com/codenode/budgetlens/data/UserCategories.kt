package com.codenode.budgetlens.data

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import org.json.JSONArray
import java.util.concurrent.CountDownLatch

class UserCategories {
    companion object {
        var userCategories = mutableListOf<Category>()

        fun loadCategoriesFromAPI(context: Context): MutableList<Category> {
            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"

            val registrationPost = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .method("GET", body = null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()
            val countDownLatch = CountDownLatch(1)
            registrationPost.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful) {
                            if (responseBody != null) {
                                val categoryList = JSONArray(responseBody.toString())
                                for (i in 0 until categoryList.length()) {
                                    val category = categoryList.getJSONObject(i)
                                    val id = category.getInt("id")
                                    val categoryName = category.getString("category_name")
                                    val categoryToggleStar =
                                        category.getBoolean("category_toggle_star")
                                    userCategories.add(
                                        Category(
                                            id,
                                            categoryName,
                                            categoryToggleStar,
                                            null
                                        )
                                    )

                                    // Add the SubCategories to the list as well
                                    val subCategoryList = category.getJSONArray("sub_category_list")
                                    for (i in 0 until subCategoryList.length()) {
                                        val subCategory = subCategoryList.getJSONObject(i)
                                        val subId = -1 // Another way of distinguishing between subCategoryies
                                        val subCategoryName = subCategory.getString("category_name")
                                        val subCategoryToggleStar =
                                            subCategory.getBoolean("category_toggle_star")
                                        userCategories.add(
                                            Category(
                                                subId,
                                                subCategoryName,
                                                subCategoryToggleStar,
                                                id // From category variable
                                            )
                                        )
                                    }
                                    Log.d("Debug Adding", "Added category")
                                }
                            }

                            Log.i(
                                "Successful",
                                "Displayed Category string list successfully${responseBody}"
                            )
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong${responseBody} ${response.message} ${response.headers}"
                            )
                        }
                    }
                    countDownLatch.countDown()
                }
            })

            // wait for a response before returning
            countDownLatch.await()
            Log.d("Debug List", userCategories.toString())
            return userCategories
        }
    }
}