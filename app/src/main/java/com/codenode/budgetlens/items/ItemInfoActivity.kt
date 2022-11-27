package com.codenode.budgetlens.items

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ItemInfoActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var itemPrice: TextView
    private lateinit var itemName: TextView
    private lateinit var itemOwner: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_item_info)
        //get the item Id from the previous page

        val itemId: String? = intent.getStringExtra("itemId")

        //create category arrays
        //here is where you get the array from the database
        val categories = resources.getStringArray(R.array.category_place_holder)

        //apply the adapter to the dropdown menu
        val arrayAdapter = ArrayAdapter(this, R.layout.category_dropdown_item, categories)
        autoCompleteTextView = findViewById(R.id.category_dropdown)
        autoCompleteTextView.setAdapter(arrayAdapter)

        //setup item display fields
        itemPrice = findViewById(R.id.item_info_price)
        itemName = findViewById(R.id.item_info_name)
        itemOwner = findViewById(R.id.item_original_owner)


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
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val itemArray = JSONArray(responseBody.toString())
                            val item = itemArray.getJSONObject(0)
                            val price = item.getString("price")
                            val name = item.getString("name")
                            val user = item.getString("user")
                            runOnUiThread {
                                itemPrice.text = price
                                itemName.text = name
                                itemOwner.text = user
                            }


                        } else {
                            Log.i(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    } else {
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