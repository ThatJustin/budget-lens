package com.codenode.budgetlens.items

import android.app.Activity
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.InputType.*
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.common.Utilities
import com.codenode.budgetlens.data.ImportantDates
import com.codenode.budgetlens.data.UserImportantDates
import com.codenode.budgetlens.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private lateinit var categoryDropDown: AutoCompleteTextView
    private lateinit var importantDatesList: MutableList<ImportantDates>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var importantDatesRecyclerView: RecyclerView? = null
    private lateinit var datesAdapter: RecyclerView.Adapter<ImportantDatesRecyclerViewAdapter.ViewHolder>
    private lateinit var itemPrice: TextView
    private lateinit var itemName: TextView
    private lateinit var itemOwner: TextView
    private var newItemName: String = ""
    private var newItemPrice: String = ""
    var categoryMap = mutableMapOf<Int, String>()
    private var localPrice = 0.0

    val activity = this as Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_item_info)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)

        //get the item Id from the previous page

        val itemId: String? = intent.getStringExtra("itemId")
        val position: Int = intent.getIntExtra("position", -1)

        //setup item display fields
        itemPrice = findViewById(R.id.item_info_price)
        itemName = findViewById(R.id.item_info_name)
        itemOwner = findViewById(R.id.item_original_owner)

        handleCategories(itemId)
        handleGetItemData(itemId)
        handleAdapter(itemId)
        handleDeleteItem(itemId, position)
        handleEditItemPrice(itemId, position)
        handleEditItemName(itemId, position)
        handleAddImportantDateButton(itemId)
    }

    private fun handleGetItemData(itemId: String?) {
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
                                categoryDropDown.setText(item.getString("category_name"), false)
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
    }

    /**
     * Handles the item category loading and assigning.
     */
    private fun handleCategories(itemId: String?) {
        //Load the categories
        val categoryItemsMap = loadCategories()
        val categoryItems: MutableList<String> = categoryItemsMap.values.toMutableList()
            .sortedBy { it.lowercase() } as MutableList<String>

        //apply the adapter to the dropdown menu
        val arrayAdapter = ArrayAdapter(this, R.layout.list_items, categoryItems)
        categoryDropDown = findViewById(R.id.category_dropdown)
        categoryDropDown.setAdapter(arrayAdapter)

        //Handle when the dropdonn is clicked
        categoryDropDown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // get the selected item
                val item = parent.getItemAtPosition(position)

                // show a prompt or perform any other action here
                MaterialAlertDialogBuilder(this)
                    .setTitle("Is ${itemName.text} Always labeled as $item?")
                    .setMessage("If it is, this spending and all spendings under this label will change to $item,")
                    .setPositiveButton("Apply To All Spendings") { _, _ ->
                        val regexValue = itemName.text
                        val itemId = id
                        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/rules/add/"

                        // make the post request
                        val request = Request.Builder()
                            .url(url)
                            .url(url)
                            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
                            .addHeader("Content-Type", "application/json")
                            .post(
                                FormBody.Builder()
                                    .add("regex", regexValue.toString())
                                    .add("category", itemId.toString())
                                    .build()
                            )
                            .build()

                        val client = OkHttpClient()
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                // handle the failure
                            }

                            override fun onResponse(call: Call, response: Response) {
                                if (response.isSuccessful) {
                                    runOnUiThread {
                                        Toast.makeText(
                                            applicationContext,
                                            "Successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    // handle the unsuccessful response
                                }
                            }
                        })
                    }
                    .setNegativeButton("Just this spending") { dialog, _ ->
                        val newItemCategory = id
                        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/$itemId/"
                        val registrationPost = OkHttpClient()
                        val mediaType = "application/json".toMediaTypeOrNull()

                        val body = ("{\r\n" +
                                "    \"category_id\": \"${newItemCategory}\"\r\n" +
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
                                            Log.i("Successful", "Item ${itemName.text} edited.")
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

    private fun handleAddImportantDateButton(itemId: String?) {
        val addImportantDate = findViewById<Button>(R.id.item_add_important_date)

        //Encapsulate everything in a LinearLayout
        val layout = LinearLayout(this, null, 0)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 0, 50, 0)

        // An EditText Field will be used for the title
        val description = EditText(this)
        description.maxLines = 1
        //backend has a max length of 36, respect it here
        description.filters = arrayOf(InputFilter.LengthFilter(36))
        description.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(description)

        //Calendar to select a date
        val calendar = CalendarView(this)
        var selectedDate = Utilities.convertDateToFormat(
            calendar.date,
            "yyyy-MM-dd"
        ) // by default the current date

        //Update with the newly selected date
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate =
                "$year-${String.format("%02d", (month + 1))}-${String.format("%02d", dayOfMonth)}"
        }

        layout.addView(calendar)

        //Create the dialog
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Important Date")
            .setMessage("Enter a description for the date and select the date.\r\n\r\nDescription:")
            .setView(layout)
            .setNegativeButton("Cancel") { dialog, _ ->
                description.setText("")
                dialog.dismiss()
            }
            .setPositiveButton("Add", null)
            .create()

        // Check when a user inputs a title to enable/disable the Add button
        description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dialog.getButton(BUTTON_POSITIVE).isEnabled = s.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        addImportantDate.setOnClickListener {

            dialog.show()
            dialog.getButton(BUTTON_POSITIVE).isEnabled = false

            dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
                //Request backend api to add the reminder
                val isSuccess = UserImportantDates.requestAddImportantDate(
                    this,
                    itemId.toString(),
                    selectedDate.toString(),
                    description.text.toString()
                )
                //Update the adapter and inform the user of the outcome
                addImportantDate(isSuccess)

                //clean up for next time
                description.setText("")

                dialog.dismiss()
            }
        }
    }


    private fun addImportantDate(isSuccess: Boolean) {
        val snackBarMsg = if (isSuccess) "Reminder added." else "Failed to add reminder."

        if (isSuccess) {
            //Update the list
            importantDatesList = UserImportantDates.userImportantDates

            //Added to the end, I do not know if there's a specific order yet
            datesAdapter.notifyItemInserted(importantDatesList.size - 1)
        }

        //send a snackbar to update the user of their action
        Snackbar.make(
            activity.findViewById<BottomNavigationView>(R.id.bottom_navigation),
            snackBarMsg,
            Snackbar.LENGTH_SHORT
        ).show()
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


    private fun handleAdapter(itemId: String?) {
        UserImportantDates.userImportantDates.clear()
        importantDatesList = UserImportantDates.loadImportantDatesFromAPI(this, itemId)
        Log.i("--------------", importantDatesList.toString())
        importantDatesRecyclerView = findViewById(R.id.important_dates_list)
        if (importantDatesRecyclerView != null) {
            importantDatesRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            importantDatesRecyclerView!!.layoutManager = linearLayoutManager
            datesAdapter = ImportantDatesRecyclerViewAdapter(importantDatesList)
            importantDatesRecyclerView!!.adapter = datesAdapter
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

    private fun handleEditItemPrice(itemId: String?, position: Int) {
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

    private fun loadCategories(): MutableMap<Int, String> {
        categoryMap.clear()
        //I do not think anything in the DB begins at index 0
        categoryMap[0] = ""
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category"

        val registrationPost = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .addHeader("Content-Type", "application/json")
            .build()

        val countDownLatch = CountDownLatch(1)

        registrationPost.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            val categories = JSONArray(responseBody.toString())
                            for (i in 0 until categories.length()) {
                                val category = categories.getJSONObject(i)
                                val id = category.getInt("id")
                                val categoryName = category.getString("category_name")
                                categoryMap[id] = categoryName
                            }
                            Log.i("Successful", "Successfully loaded categories from API.")
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
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        return categoryMap
    }

}