package com.codenode.budgetlens.receipts.filter

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

class ReceiptsFilterDialog(
    private val activityContext: Context,
    themeID: Int,
    private val supportFragmentManager: FragmentManager,
    private val previousFilterOptions: ReceiptsFilterOptions
) : Dialog(activityContext, themeID) {

    private val calendar = Calendar.getInstance()
    private val dateFormatString = "yyyy/MM/dd"
    private lateinit var merchantOptions: AutoCompleteTextView
    private lateinit var locationOptions: AutoCompleteTextView
    private lateinit var currencyOptions: AutoCompleteTextView
    private lateinit var couponOptions: AutoCompleteTextView
    private lateinit var activeFilters: ChipGroup
    private lateinit var merchantChip: Chip
    private lateinit var locationChip: Chip
    private lateinit var currencyChip: Chip
    private lateinit var couponChip: Chip
    private lateinit var dateChip: Chip
    private lateinit var priceChip: Chip
    private lateinit var minPrice: AutoCompleteTextView
    private lateinit var maxPrice: AutoCompleteTextView
    private lateinit var startDate: AutoCompleteTextView
    private lateinit var endDate: AutoCompleteTextView
    var isMinPriceSet = false
    var isMaxPriceSet = false

    private var receiptsFilterDialogListener: ReceiptsFilterDialogListener? = null
    var filterOptions = ReceiptsFilterOptions()
    var selectedMerchantId = -1
    var selectedLocationId = -1
    var selectedCurrencyId = -1
    var selectedCouponId = -1

    var merchantMap = mutableMapOf<Int, String>()
    var locationMap = mutableMapOf<Int, String>()
    var currencyMap = mutableMapOf<Int, String>()
    var couponMap = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView: View = layoutInflater.inflate(R.layout.receipts_filter_dialog, null)
        setContentView(dialogView)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        merchantMap.clear()
        locationMap.clear()
        currencyMap.clear()
        couponMap.clear()

        //Set listener
        receiptsFilterDialogListener = activityContext as Activity as ReceiptsFilterDialogListener

        //Active filters
        activeFilters = findViewById(R.id.active_receipts_filters_chip_group)

        // Merchant
        merchantOptions = findViewById(R.id.receipts_filter_merchant_options)

        // Location
        locationOptions = findViewById(R.id.receipts_filter_location_options)

        // Currency
        currencyOptions = findViewById(R.id.receipts_filter_currency_options)

        // Coupon
        couponOptions = findViewById(R.id.receipts_filter_coupon_options)

        //Date
        startDate = findViewById(R.id.receipts_filter_start_date)
        endDate = findViewById(R.id.receipts_filter_end_date)

        //Price
        minPrice = findViewById(R.id.item_filter_min_price)
        maxPrice = findViewById(R.id.item_filter_max_price)

        handleClosingDialog()
        handleChipClicking()
        loadFilters()
        handleMerchants()
        handleLocations()
        handleCurrencies()
        handleCoupons()
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
        //Location
        if (filterOptions.location.isNotEmpty()) {
            locationChip.visibility = View.VISIBLE
            filterOptions.locationId = filterOptions.locationId
            locationOptions.setText(filterOptions.location)
        }
        //Currency
        if (filterOptions.currency.isNotEmpty()) {
            currencyChip.visibility = View.VISIBLE
            filterOptions.currencyId = filterOptions.currencyId
            currencyOptions.setText(filterOptions.currency)
        }
        //Coupon
        if (filterOptions.coupon.isNotEmpty()) {
            couponChip.visibility = View.VISIBLE
            filterOptions.couponId = filterOptions.couponId
            couponOptions.setText(filterOptions.coupon)
        }
        //Date
        if (filterOptions.startDate != 0L && filterOptions.endDate != 0L) {
            dateChip.visibility = View.VISIBLE
            startDate.setText(filterOptions.startDate.toString())
            endDate.setText(filterOptions.endDate.toString())
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
     * Updates the start and end date text fields, their visibility
     */
    private fun updateDateSelection(start: Long, end: Long, visible: Int) {
        filterOptions.startDate = start
        filterOptions.endDate = end
        dateChip.visibility = visible
        updateDateTextView(start, end)
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
            calendar.timeInMillis = start

            var dateFormat = SimpleDateFormat(dateFormatString, Locale.CANADA)
            startDate.setText(dateFormat.format(calendar.time))

            //update end date
            calendar.timeInMillis = end
            dateFormat = SimpleDateFormat(dateFormatString, Locale.CANADA)
            endDate.setText(dateFormat.format(calendar.time))
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
                    locationChip = chip
                }
                2 -> {
                    currencyChip = chip
                }
                3 -> {
                    couponChip = chip
                }
                4 -> {
                    dateChip = chip
                }
                5 -> {
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
                locationChip.isChecked = true
                filterOptions.location = ""
                filterOptions.locationId = -1
                locationOptions.text.clear()
            }
            2 -> {
                currencyChip.isChecked = true
                filterOptions.currency = ""
                filterOptions.currencyId = -1
                currencyOptions.text.clear()
            }
            3 -> {
                couponChip.isChecked = true
                filterOptions.coupon = ""
                filterOptions.couponId = -1
                couponOptions.text.clear()
            }
            4 -> {
                filterOptions.startDate = 0
                filterOptions.endDate = 0
                dateChip.visibility = View.GONE
                startDate.setText("")
                endDate.setText("")
            }
            5 -> {
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
    private fun handleMerchants() {
        val merchantReceiptsMap = loadMerchants()
        println("merchantReceiptsMap $merchantReceiptsMap")
        val merchantReceipts: MutableList<String> = merchantReceiptsMap.values.toMutableList().distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, merchantReceipts)
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

    /**
     * Handles location filter.
     */
    private fun handleLocations() {
        val locationReceiptsMap = loadLocations()
        val locationReceipts: MutableList<String> = locationReceiptsMap.values.toMutableList().distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, locationReceipts)
        locationOptions.setAdapter(adapter)
        locationOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.location = ""
            filterOptions.locationId = -1
            locationChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.location = value
                filterOptions.locationId = getKeyByValue(locationMap, value)
                locationChip.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles currency filter.
     */
    private fun handleCurrencies() {
        val currencyReceiptsMap = loadCurrencies()
        val currencyReceipts: MutableList<String> = currencyReceiptsMap.values.toMutableList().distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, currencyReceipts)
        currencyOptions.setAdapter(adapter)
        currencyOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.currency = ""
            filterOptions.currencyId = -1
            currencyChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.currency = value
                filterOptions.currencyId = getKeyByValue(currencyMap, value)
                currencyChip.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles coupon filter.
     */
    private fun handleCoupons() {
        val couponReceiptsMap = loadCoupons()
        val couponReceipts: MutableList<String> = couponReceiptsMap.values.toMutableList().distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, couponReceipts)
        couponOptions.setAdapter(adapter)
        couponOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.coupon = ""
            filterOptions.couponId = -1
            couponChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.coupon = value
                filterOptions.couponId = getKeyByValue(couponMap, value)
                couponChip.visibility = View.VISIBLE
            }
        }
    }

    private fun <K, V> getKeyByValue(hashMap: Map<K, V>, target: V): K {
        return hashMap.filter { target == it.value }.keys.first()
    }

    private fun loadMerchants(): MutableMap<Int, String> {
        var pageNumber = 1
        merchantMap.clear()
        var contentLoadedFromResponse = true

        while (contentLoadedFromResponse) {
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/pageNumber=${pageNumber}&pageSize=5/"
            contentLoadedFromResponse = false

            val receiptsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()

            val countDownLatch = CountDownLatch(1)

            receiptsRequest.newCall(request).enqueue(object : Callback {
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
                                val pageList =
                                    JSONObject(responseBody.toString()).getString("page_list")
                                val receipts = JSONArray(pageList)
                                for (i in 0 until receipts.length()) {
                                    contentLoadedFromResponse = true
                                    val receipt = receipts.getJSONObject(i)
                                    val id = receipt.getInt("id")
                                    val merchant = receipt.getString("merchant")
                                    merchantMap[id] = merchant
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded merchants from API.")
                            } else {
                                contentLoadedFromResponse = false
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
        }
        return merchantMap
    }

    private fun loadLocations(): MutableMap<Int, String> {
        var pageNumber = 1
        locationMap.clear()
        var contentLoadedFromResponse = true

        while (contentLoadedFromResponse) {
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/pageNumber=${pageNumber}&pageSize=5/"
            contentLoadedFromResponse = false

            val receiptsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()

            val countDownLatch = CountDownLatch(1)

            receiptsRequest.newCall(request).enqueue(object : Callback {
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
                                val pageList =
                                    JSONObject(responseBody.toString()).getString("page_list")
                                val receipts = JSONArray(pageList)
                                for (i in 0 until receipts.length()) {
                                    contentLoadedFromResponse = true
                                    val receipt = receipts.getJSONObject(i)
                                    val id = receipt.getInt("id")
                                    val location = receipt.getString("location")
                                    locationMap[id] = location
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded locations from API.")
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
        }
        return locationMap
    }

    private fun loadCurrencies(): MutableMap<Int, String> {
        var pageNumber = 1
        currencyMap.clear()
        var contentLoadedFromResponse = true

        while (contentLoadedFromResponse) {
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/pageNumber=${pageNumber}&pageSize=5/"
            contentLoadedFromResponse = false

            val receiptsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()

            val countDownLatch = CountDownLatch(1)

            receiptsRequest.newCall(request).enqueue(object : Callback {
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
                                val pageList =
                                    JSONObject(responseBody.toString()).getString("page_list")
                                val receipts = JSONArray(pageList)
                                for (i in 0 until receipts.length()) {
                                    contentLoadedFromResponse = true
                                    val receipt = receipts.getJSONObject(i)
                                    val id = receipt.getInt("id")
                                    val currency = receipt.getString("currency")
                                    currencyMap[id] = currency
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded currencies from API.")
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
        }
        return currencyMap
    }

    private fun loadCoupons(): MutableMap<Int, String> {
        var pageNumber = 1
        couponMap.clear()
        var contentLoadedFromResponse = true

        while (contentLoadedFromResponse) {
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/pageNumber=${pageNumber}&pageSize=5/"
            contentLoadedFromResponse = false

            val receiptsRequest = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()

            val countDownLatch = CountDownLatch(1)

            receiptsRequest.newCall(request).enqueue(object : Callback {
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
                                val pageList =
                                    JSONObject(responseBody.toString()).getString("page_list")
                                val receipts = JSONArray(pageList)
                                for (i in 0 until receipts.length()) {
                                    contentLoadedFromResponse = true
                                    val receipt = receipts.getJSONObject(i)
                                    val id = receipt.getInt("id")
                                    val coupon = receipt.getString("coupon")
                                    couponMap[id] = coupon
                                }
                                if (contentLoadedFromResponse) {
                                    pageNumber++
                                }
                                Log.i("Successful", "Successfully loaded coupons from API.")
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
        }
        return couponMap
    }

    /**
     * Handles the start and end date setOnClickListener which calls openDateRangePicker.
     */
    private fun handleStartEndDate() {
        startDate.setOnClickListener {
            openDateRangePicker()
        }

        maxPrice.setOnClickListener {
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
        val closeDialog = findViewById<ImageButton>(R.id.filter_receipts_dialog_close)
        closeDialog.setOnClickListener {
            receiptsFilterDialogListener?.onReturnedFilterOptions(filterOptions)
            this.dismiss()
        }
    }
}