package com.codenode.budgetlens.items.filter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class ItemsFilterDialog(
    private val activityContext: Context,
    themeID: Int,
    private val supportFragmentManager: FragmentManager,
    private val previousFilterOptions: ItemsFilterOptions,
    private val isFromSingleReceipt: Boolean = false
) : Dialog(activityContext, themeID) {

    private val calendar = Calendar.getInstance()
    private val dateFormatString = "yyyy-MM-dd"
    private lateinit var merchantOptions: AutoCompleteTextView
    private lateinit var categoryOptions: AutoCompleteTextView
    private lateinit var activeFilters: ChipGroup
    private lateinit var merchantChip: Chip
    private lateinit var categoryChip: Chip
    private lateinit var dateChip: Chip
    private lateinit var priceChip: Chip
    private lateinit var minPrice: AutoCompleteTextView
    private lateinit var maxPrice: AutoCompleteTextView
    private lateinit var startDate: AutoCompleteTextView
    private lateinit var endDate: AutoCompleteTextView
    var isMinPriceSet = false
    var isMaxPriceSet = false

    private var itemsFilterDialogListener: ItemsFilterDialogListener? = null
    var filterOptions = ItemsFilterOptions()

    var categoryMap = mutableMapOf<Int, String>()
    var merchantMap = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView: View = layoutInflater.inflate(R.layout.items_filter_dialog, null)
        setContentView(dialogView)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        categoryMap.clear()

        //Set listener
        itemsFilterDialogListener = activityContext as Activity as ItemsFilterDialogListener

        //Active filters
        activeFilters = findViewById(R.id.active_items_filters_chip_group)

        // Merchant
        merchantOptions = findViewById(R.id.items_filter_merchant_options)

        //Category
        categoryOptions = findViewById(R.id.items_filter_category_options)

        //Date
        startDate = findViewById(R.id.items_filter_start_date)
        endDate = findViewById(R.id.items_filter_end_date)

        //Price
        minPrice = findViewById(R.id.items_filter_min_price)
        maxPrice = findViewById(R.id.items_filter_max_price)

        handleClosingDialog()
        handleChipClicking()
        loadFilters()
        handleMerchant()
        handleCategory()
        handleStartEndDate()
        handlePriceRange()
    }

    /**
     * Loads previously applied filters.
     */
    private fun loadFilters() {
        //Update the filters to the previously selected ones
        this.filterOptions = previousFilterOptions

        //Merchant
        if (filterOptions.merchantName.isNotEmpty()) {
            merchantChip.visibility = View.VISIBLE
            filterOptions.merchantId = filterOptions.merchantId
            merchantOptions.setText(filterOptions.merchantName)
        }
        //Category
        if (filterOptions.categoryName.isNotEmpty()) {
            categoryChip.visibility = View.VISIBLE
            filterOptions.categoryId = filterOptions.categoryId
            categoryOptions.setText(filterOptions.categoryName)
        }
        //Date
        if (filterOptions.startDate.isNotEmpty() && filterOptions.endDate.isNotEmpty()) {
            dateChip.visibility = View.VISIBLE
            startDate.setText(filterOptions.startDate)
            endDate.setText(filterOptions.endDate)
        }
        //Price
        if (filterOptions.minPrice != 0.0 && filterOptions.maxPrice != 0.0) {
            priceChip.visibility = View.VISIBLE
            minPrice.setText(filterOptions.minPrice.toString())
            maxPrice.setText(filterOptions.maxPrice.toString())
        }

    }

    /**
     * Creates and opens a date range picker.
     * Overrides:
     * addOnPositiveButtonClickListener: calls updateDateSelection with updated date and view VISIBLE
     * addOnNegativeButtonClickListener: calls updateDateSelection with 0 dates and view GONE
     * addOnCancelListener: calls updateDateSelection with 0 dates and view GONE
     */
    private fun openDateRangePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()
        dateRangePicker.show(supportFragmentManager, "ItemFilterDateRange")
        dateRangePicker.addOnPositiveButtonClickListener {
            updateDateSelection(it.first, it.second, View.VISIBLE)
        }
        dateRangePicker.addOnNegativeButtonClickListener {
            updateDateSelection(0, 0, View.GONE)
        }
        dateRangePicker.addOnCancelListener {
            updateDateSelection(0, 0, View.GONE)
        }
    }

    /**
     * Updates the start and end date text fields, their visibility and
     */
    private fun updateDateSelection(start: Long, end: Long, visible: Int) {
        filterOptions.startDate = getDateFromMilliseconds(start)
        filterOptions.endDate = getDateFromMilliseconds(end)
        dateChip.visibility = visible
        updateDateTextView(start, end)
    }

    private fun getDateFromMilliseconds(time: Long): String {
        calendar.timeInMillis = time
        calendar.add(Calendar.DATE, 1)
        val dateFormat = SimpleDateFormat(dateFormatString, Locale.CANADA)
        return dateFormat.format(calendar.time)
    }

    /**
     * Updates the start date and end date text views with the input millisecond timestamp.
     */
    private fun updateDateTextView(start: Long, end: Long) {
        if (start == 0L && end == 0L) { /// user did not save the selected date
            startDate.setText("")
            endDate.setText("")
        } else {
            //Update start date
            startDate.setText(getDateFromMilliseconds(start))

            //update end date
            endDate.setText(getDateFromMilliseconds(end))
        }
    }

    /**
     * Adds a listener and handles what chips are clicked on.
     */
    private fun handleChipClicking() {
        for (index in 0 until activeFilters.childCount) {
            val chip: Chip = activeFilters.getChildAt(index) as Chip
            when (index) {
                0 -> {
                    merchantChip = chip
                }
                1 -> {
                    categoryChip = chip
                }
                2 -> {
                    dateChip = chip
                }
                3 -> {
                    priceChip = chip
                }
            }
            chip.setOnClickListener { view ->
                if (view.visibility == View.VISIBLE) {
                    view.visibility = View.GONE
                    onHideChip(index)
                }
            }
        }
    }

    /**
     * Reverts that selected filter previously selected.
     */
    private fun onHideChip(index: Int) {
        when (index) {
            0 -> {
                merchantChip.isChecked = true
                filterOptions.merchantName = ""
                filterOptions.merchantId = -1
                merchantOptions.text.clear()
            }
            1 -> {
                categoryChip.isChecked = true
                filterOptions.categoryName = ""
                filterOptions.categoryId = -1
                categoryOptions.text.clear()
            }
            2 -> {
                filterOptions.startDate = ""
                filterOptions.endDate = ""
                dateChip.visibility = View.GONE
                startDate.setText("")
                endDate.setText("")
            }
            3 -> {
                filterOptions.minPrice = 00.00
                filterOptions.maxPrice = 00.00
                priceChip.isChecked = true
                isMinPriceSet = false
                isMaxPriceSet = false
                minPrice.text.clear()
                maxPrice.text.clear()
            }
        }
    }

    /**
     * Handles merchant filter.
     */
    private fun handleMerchant() {
        // If this activity is from viewing a receipts items, there is no merchant to filter
        // since it's all from the same receipt (same merchant)
        if (isFromSingleReceipt) {
            //Hide the merchant filter option in the UI
            val merchantConstraint = findViewById<ConstraintLayout>(R.id.merchantConstraint)
            merchantConstraint.visibility = View.GONE
        } else {
            val merchantNamesMap = loadMerchantNames()
            val merchantNames: MutableList<String> = merchantNamesMap.values.toMutableList()
                .sortedBy { it.lowercase() } as MutableList<String>

            val adapter = ArrayAdapter(context, R.layout.list_items, merchantNames)
            merchantOptions.setAdapter(adapter)

            merchantOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
                filterOptions.merchantName = ""
                filterOptions.merchantId = -1
                merchantChip.visibility = View.GONE
                val value = adapter.getItem(pos) ?: ""
                if (value.isNotEmpty()) {
                    filterOptions.merchantName = value
                    filterOptions.merchantId = getKeyByValue(merchantMap, value)
                    merchantChip.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Loads merchants names for the merchant filter.
     */
    private fun loadMerchantNames(): MutableMap<Int, String> {
        merchantMap.clear()
        //I do not think anything in the DB begins at index 0
        categoryMap[0] = ""
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/merchant"

        val registrationPost = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
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
                            val merchantsArr =
                                JSONObject(responseBody.toString()).getString("merchants")
                            val merchants = JSONArray(merchantsArr)
                            for (i in 0 until merchants.length()) {
                                val merchant = merchants.getJSONObject(i)
                                val id = merchant.getInt("id")
                                val name = merchant.getString("name")
                                if (name.isNotEmpty()) {
                                    merchantMap[id] = name
                                }
                            }
                            Log.i("Successful", "Successfully loaded merchant names from API.")
                        } else {
                            Log.i(
                                "Error",
                                "Something went wrong \r\n${response.message} ${response.headers}"
                            )
                        }

                    } else {
                        Log.e(
                            "Error",
                            "Something went wrong \r\n${response.message} ${response.headers}"
                        )
                    }
                }
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        return merchantMap
    }

    private fun <K, V> getKeyByValue(hashMap: Map<K, V>, target: V): K {
        return hashMap.filter { target == it.value }.keys.first()
    }

    /**
     * Handles category filter.
     */
    private fun handleCategory() {
        val categoryItemsMap = loadCategories()
        val categoryItems: MutableList<String> = categoryItemsMap.values.toMutableList()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_items, categoryItems)
        categoryOptions.setAdapter(adapter)
        categoryOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.categoryName = ""
            filterOptions.categoryId = -1
            categoryChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.categoryName = value
                filterOptions.categoryId = getKeyByValue(categoryMap, value)
                categoryChip.visibility = View.VISIBLE
            }
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
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
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

    /**
     * Handles the start and end date setOnClickListener which calls openDateRangePicker.
     */
    private fun handleStartEndDate() {
        startDate.setOnClickListener {
            openDateRangePicker()
        }

        endDate.setOnClickListener {
            openDateRangePicker()
        }
    }


    /**
     * Handles the min and max price inputs.
     * Detects when user enters valid prices and shows/hides the price chip when needed.
     */
    private fun handlePriceRange() {
        /**
         * Toggles the price chip.
         */
        fun togglePriceChip() {
            priceChip.visibility = if (isMinPriceSet && isMaxPriceSet) View.VISIBLE else View.GONE
        }

        /**
         * Ensures the input text when using a decimal is at most 2 numbers.
         * @author https://stackoverflow.com/a/24397810
         */
        fun enforceValidDecimalPosition(s: String, maxDecimalDigits: Int): String {
            var str = s
            var result = ""
            var after = false
            var pos = 0
            var decimalPos = 0

            if (str[0] == '.') {
                str = "00$str"
            }

            while (pos < str.length) {
                if (str[pos] == '.' || after) {
                    if (str[pos] == '.') {
                        after = true
                    } else {
                        decimalPos++
                        if (decimalPos > maxDecimalDigits) {
                            return result
                        }
                    }
                }
                result += str[pos]
                pos++
            }
            return result
        }

        minPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                isMinPriceSet = if (text.contains(".")) {
                    text.substring(text.indexOf("."), text.length).length == 3
                } else {
                    text.isNotEmpty()
                }
                if (text.isNotEmpty()) {
                    filterOptions.minPrice = text.toDouble()
                } else {
                    filterOptions.minPrice = 00.00
                }
                togglePriceChip()
            }

            //Author https://stackoverflow.com/a/24397810
            override fun afterTextChanged(s: Editable) {
                val str: String = s.toString()
                if (str.isEmpty()) return
                val str2 = enforceValidDecimalPosition(str, 2)
                if (str2 != str) {
                    if (str2 is String) {
                        minPrice.setText(str2)
                        minPrice.setSelection(str2.length)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })

        maxPrice.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                isMaxPriceSet = if (text.contains(".")) {
                    text.substring(text.indexOf("."), text.length).length == 3
                } else {
                    text.isNotEmpty()
                }
                if (text.isNotEmpty()) {
                    filterOptions.maxPrice = text.toDouble()
                } else {
                    filterOptions.maxPrice = 00.00
                }
                togglePriceChip()
            }

            //Author https://stackoverflow.com/a/24397810
            override fun afterTextChanged(s: Editable) {
                val str: String = s.toString()
                if (str.isEmpty()) return
                val str2 = enforceValidDecimalPosition(str, 2)
                if (str2 != str) {
                    if (str2 is String) {
                        maxPrice.setText(str2)
                        maxPrice.setSelection(str2.length)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })
    }

    /**
     * Handles closing the dialog.
     */
    private fun handleClosingDialog() {
        val closeDialog = findViewById<TextView>(R.id.filter_items_dialog_close)
        closeDialog.setOnClickListener {
            itemsFilterDialogListener?.onReturnedFilterOptions(filterOptions)
            this.dismiss()
        }
    }
}