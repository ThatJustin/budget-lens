package com.codenode.budgetlens.items

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ItemInfoActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_item_info)

        //create category arrays
        //here is where you get the array from the database
        val categories = resources.getStringArray(R.array.category_place_holder)

        //apply the adapter to the dropdown menu
        val arrayAdapter = ArrayAdapter(this, R.layout.category_dropdown_item, categories)
        autoCompleteTextView = findViewById(R.id.category_dropdown)
        autoCompleteTextView.setAdapter(arrayAdapter)

        //get the item Id from the previous page
        val itemId = intent.getStringExtra("itemId")

        //get the item data
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/${itemId}/"
        val itemsRequest = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .addHeader("Content-Type", "application/json")
            .build()
        itemsRequest.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use{
                    if(response.isSuccessful){
                        val responseBody=response.body?.string()
                        if(responseBody!=null){
                            val price = JSONObject(responseBody.toString()).getString("price")
                            val name = JSONObject(responseBody.toString()).getString("name")
                            val user =  JSONObject(responseBody.toString()).getString("user")

                        }else {
                            Log.i(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    }else {
                        Log.e(
                            "Error",
                            "Something went wrong ${response.message} ${response.headers}"
                        )
                    }
                }
            }
        })
    }
}