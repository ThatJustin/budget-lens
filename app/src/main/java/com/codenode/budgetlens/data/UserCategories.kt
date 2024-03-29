package com.codenode.budgetlens.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.util.concurrent.CountDownLatch

class UserCategories {
    companion object {
        var userCategories = mutableListOf<Category>()

        /**
         * Updates the star when the user clicks on the star Image of the given field.
         * The API is called in the recycler view for the Category `CategoryRecyclerViewAdapter`
         *
         * `category`: The category that the user clicked the star for
         *
         * `imageStar`: The ImageView of the star in order for the star to be updated
         *
         */
        fun toggleStarFromAPI(context: Context, category: Category, imageStar: ImageView) {

            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/categoryName=${category.category_name}"

            val toggleStarUpdate = OkHttpClient()

            // There is no request body for this PUT method, but the request builder requires
            // one which is why this body is created with empty `addFormDataPart`
            val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("", "")
                .build()

            val request = Request.Builder()
                .url(url)
                .method("PUT", body = body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()

            toggleStarUpdate.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful) {
                            if (category.category_toggle_star) {
                                category.category_toggle_star = false
                                imageStar.setImageResource(R.drawable.ic_baseline_star_outline_24)
                            } else {
                                category.category_toggle_star = true
                                imageStar.setImageResource(R.drawable.ic_baseline_star_24)
                            }
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong${responseBody} ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }

        fun deleteSubCategoryFromAPI(
            context: Context,
            category: Category
        ) {

            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/categoryName=${category.category_name}"

            val toggleStarUpdate = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .method("DELETE", body = null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()

            toggleStarUpdate.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                @SuppressLint("NotifyDataSetChanged")
                @Suppress("Thread")
                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful) {
                            // Remove the category from the list in the frontend list
                            userCategories.remove(category)
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong${responseBody} ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }

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
                                // Turn the response body into an array for the category list
                                val categoryList = JSONArray(responseBody.toString())
                                for (i in 0 until categoryList.length()) {
                                    val category = categoryList.getJSONObject(i)
                                    val id = category.getInt("id")
                                    val categoryName = category.getString("category_name")
                                    val categoryToggleStar =
                                        category.getBoolean("category_toggle_star")
                                    val icon = category.getString("icon")

                                    // Create the new category for the user
                                    userCategories.add(
                                        Category(
                                            id,
                                            categoryName,
                                            categoryToggleStar,
                                            null,
                                            icon
                                        )
                                    )

                                    // Add the SubCategories to the list as well
                                    val subCategoryList = category.getJSONArray("sub_category_list")
                                    for (i in 0 until subCategoryList.length()) {
                                        val subCategory = subCategoryList.getJSONObject(i)
                                        val subId =
                                            -1 // Another way of distinguishing between subCategoryies
                                        val subCategoryName = subCategory.getString("category_name")
                                        val subCategoryToggleStar =
                                            subCategory.getBoolean("category_toggle_star")
                                        val subCategoryIcon = category.getString("icon")

                                        // For now add subcategories the same way as a parent category
                                        // TODO: Make categories and subcategories different. Either create two
                                        //  different data classes (Category and SubCategory), or update the items
                                        //  in the recycler view
                                        //
                                        userCategories.add(
                                            Category(
                                                subId,
                                                subCategoryName,
                                                subCategoryToggleStar,
                                                id,
                                                subCategoryIcon
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

        fun createNewSubCategoryFromAPI(context: Context, parentCategory: Category, categoryText: EditText, icon: String){
            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"

            val registrationPost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            val body = ("{\n" +
                    "    \"category_name\": \"${categoryText.text}\",\n" +
                    "    \"category_toggle_star\": \"False\",\n" +
                    "    \"parent_category_id\": \"${parentCategory.category_id}\",\n" +
                    "    \"icon\": \"${icon}\"\n" +
                    "}").trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .method("POST", body = body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()
            val countDownLatch = CountDownLatch(1)
            registrationPost.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        Log.i("Response", "Got the response from server")
                        response.use {
                            if (response.isSuccessful) {
                                Log.i("Successful", "Category created!")
                            } else {
                                Log.e(
                                    "Error",
                                    "Something went wrong"
                                )
                            }
                        }
                    }
                    countDownLatch.countDown()
                }
            })

            // wait for a response before returning
            countDownLatch.await()
        }

        fun editCategory(context: Context, Category: Category, categoryText: EditText){
            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/${Category.category_name}/"

            val registrationPost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            val body = ("{\n" +
                    "    \"category_name\": \"${categoryText.text}\"\n" +
                    "}").trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .method("PUT", body = body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()
            val countDownLatch = CountDownLatch(1)
            registrationPost.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        Log.i("Response", "Got the response from server")
                        response.use {
                            if (response.isSuccessful) {
                                Log.i("Successful", "Category edited!")
                            } else {
                                Log.e(
                                    "Error",
                                    "Something went wrong"
                                )
                            }
                        }
                    }
                    countDownLatch.countDown()
                }
            })

            // wait for a response before returning
            countDownLatch.await()
        }
    }
}