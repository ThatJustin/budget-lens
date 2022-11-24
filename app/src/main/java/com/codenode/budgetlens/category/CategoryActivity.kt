package com.codenode.budgetlens.category

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class CategoryActivity : AppCompatActivity() {
    // Update the UI Thread for this page
    fun createCategoryTabs(responseBody: String) {
        findViewById<TextView>(R.id.category_text).text = responseBody
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Get the context from this object in order to get the token for accessing the API calls
        val context: Context = this

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.CATEGORY_SETTINGS, this, this.window.decorView)

        val url =
            "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"

        val registrationPost = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .method("GET", body = null)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
            .build()

        registrationPost.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: java.io.IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    val responseBody = response.body?.string().toString()
                    if (response.isSuccessful) {
                        runOnUiThread {
                            createCategoryTabs(responseBody)
                        }

                        Log.i("Successful", "Displayed Category string list successfully${responseBody}")
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
}