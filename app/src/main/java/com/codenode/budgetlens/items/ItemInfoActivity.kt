package com.codenode.budgetlens.items

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType.*
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.CountDownLatch

class ItemInfoActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var itemPrice: TextView
    private lateinit var itemName: TextView
    private lateinit var itemOwner: TextView
    private var newItemName: String = ""
    private var newItemPrice: String = ""

    private var localPrice = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_item_info)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)
        //get the item Id from the previous page

        val itemId: String? = intent.getStringExtra("itemId")
        val position: Int = intent.getIntExtra("position", -1)

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
                            localPrice = price.toDouble()

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

        handleDeleteItem(itemId, position)
        handleEditItemPrice(itemId, position)
        handleEditItemName(itemId, position)
    }

    private fun handleDeleteItem(itemId: String?, position: Int) {
        findViewById<Button>(R.id.item_info_delete)?.setOnClickListener {

            MaterialAlertDialogBuilder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this Item?\r\nThe action cannot be undone.")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Delete") { dialog, _ ->
                    requestItemDeletion(dialog, itemId, position)
                }
                .show()
        }
    }

    private fun requestItemDeletion(dialog: DialogInterface, itemId: String?, position: Int) {
        var success = false
        val url =
            "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/delete/$itemId/"

        val registrationPost = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .delete()
            .build()
        val countDownLatch = CountDownLatch(1)
        registrationPost.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                success = false
                e.printStackTrace()
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            success = true
                            Log.i("Successful", "Item ID $itemId deleted.")
                        } else {
                            Log.i(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    } else {
                        Snackbar.make(
                            findViewById(R.id.toolbar),
                            "Failed to delete.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        Log.e(
                            "Error",
                            "Something went wrong ${response.message} ${response.headers}"
                        )
                    }
                }
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        if (success) {
            dialog.dismiss()
            val intent = Intent(this, ItemsListPageActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("price", localPrice)
            setResult(ItemsListPageActivity.ITEM_INFO_ACTIVITY, intent)
            finish()
        }
    }

    private fun handleEditItemPrice(itemId: String?, position: Int){
        findViewById<TextView>(R.id.item_info_price)?.setOnClickListener {
            val editItemName: EditText = EditText(this)
            editItemName.inputType = TYPE_CLASS_TEXT
            val editItemPrice: EditText = EditText(this)
            editItemPrice.inputType = TYPE_CLASS_PHONE

            MaterialAlertDialogBuilder(this)
                .setTitle("Edit Item")
                .setMessage("Edit Item Price")
                .setView(editItemName)
                .setView(editItemPrice)
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Edit") { dialog, _ ->
                    newItemPrice = editItemPrice.text.toString()
                    val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/$itemId/"
                    val registrationPost = OkHttpClient()
                    val mediaType = "application/json".toMediaTypeOrNull()

                    val body = ("{\r\n" +
                            "    \"price\": \"${newItemPrice}\"\r\n" +
                            "}").trimIndent().toRequestBody(mediaType)

                    val request = Request.Builder()
                        .url(url)
                        .method("PATCH", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
                        .build()
                    registrationPost.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.i("Response", "Got the response from server")
                            response.use {
                                if (response.isSuccessful) {
                                    val responseBody = response.body?.string()
                                    if (responseBody != null) {
                                        Log.i("Successful", "Item ID $itemId edited.")
                                    } else {
                                        Log.i(
                                            "Error",
                                            "Something went wrong ${response.message} ${response.headers}"
                                        )
                                    }
                                }
                            }
                        }
                    })
                    recreate()
                }
                .show()
        }
    }

    private fun handleEditItemName(itemId: String?, position: Int) {
        findViewById<TextView>(R.id.item_info_name)?.setOnClickListener {
            val editItemName: EditText = EditText(this)
            editItemName.inputType = TYPE_CLASS_TEXT

            MaterialAlertDialogBuilder(this)
                .setTitle("Edit Item")
                .setMessage("Edit Item Name")
                .setView(editItemName)
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Edit") { dialog, _ ->
                    newItemName = editItemName.text.toString()
                    val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/$itemId/"
                    val registrationPost = OkHttpClient()
                    val mediaType = "application/json".toMediaTypeOrNull()

                    val body = ("{\r\n" +
                            "    \"name\": \"${newItemName}\"\r\n" +
                            "}").trimIndent().toRequestBody(mediaType)

                    val request = Request.Builder()
                        .url(url)
                        .method("PATCH", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
                        .build()
                    registrationPost.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.i("Response", "Got the response from server")
                            response.use {
                                if (response.isSuccessful) {
                                    val responseBody = response.body?.string()
                                    if (responseBody != null) {
                                        Log.i("Successful", "Item ID $itemId edited.")
                                    } else {
                                        Log.i(
                                            "Error",
                                            "Something went wrong ${response.message} ${response.headers}"
                                        )
                                    }
                                }
                            }
                        }
                    })
                    recreate()
                }
                .show()
        }
    }

}