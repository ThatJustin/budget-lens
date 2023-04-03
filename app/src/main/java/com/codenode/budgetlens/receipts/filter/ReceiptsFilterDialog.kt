package com.codenode.budgetlens.receipts.filter

import android.app.Activity
import android.app.Dialog
import android.widget.AdapterView.OnItemClickListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.UserReceipts
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ReceiptsFilterDialog(
    private val activityContext: Context,
    themeID: Int,
    private val supportFragmentManager: FragmentManager,
    private val previousFilterOptions: ReceiptsFilterOptions
) : Dialog(activityContext, themeID) {

    private val calendar = Calendar.getInstance()
    private val dateFormatString = "yyyy-MM-dd"
    private lateinit var merchantOptions: AutoCompleteTextView
    private lateinit var locationOptions: AutoCompleteTextView
    private lateinit var currencyOptions: AutoCompleteTextView
    private lateinit var couponOptions: AutoCompleteTextView
    private lateinit var totalOptions: AutoCompleteTextView
    private lateinit var activeFilters: ChipGroup
    private lateinit var merchantChip: Chip
    private lateinit var locationChip: Chip
    private lateinit var currencyChip: Chip
    private lateinit var couponChip: Chip
    private lateinit var totalChip: Chip
    private lateinit var dateChip: Chip
    private lateinit var scanDateStart: AutoCompleteTextView
    private lateinit var scanDateEnd: AutoCompleteTextView

    private var receiptsFilterDialogListener: ReceiptsFilterDialogListener? = null
    private var filterOptions = ReceiptsFilterOptions()

    var merchantMap = mutableMapOf<Int, String>()
    var locationMap = mutableMapOf<Int, String>()
    var currencyMap = mutableMapOf<Int, String>()
    var couponMap = mutableMapOf<Int, String>()
    var totalMap = mutableMapOf<Int, String>()

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
        totalMap.clear()


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

        // Total
        totalOptions = findViewById(R.id.receipts_filter_total_options)

        //Date
        scanDateStart = findViewById(R.id.receipts_filter_scan_date_start)
        scanDateEnd = findViewById(R.id.receipts_filter_scan_date_end)

        handleClosingDialog()
        handleChipClicking()
        loadFilters()

        UserReceipts.filterReceiptsMenu(activityContext as ReceiptsListPageActivity, onSuccess = {
            handleMerchants(it.merchants)
            handleLocations(it.locations)
            handleCurrencies(it.currency)
            handleCoupons(it.coupon)
            handleTotals(it.total)
        }, onFailure = {
            Log.d("ERROR@1: =>", "ERROR@1: => $it")
        })
        handleScanDateStartEnd()
    }


    /**
     * Handles the start and end date setOnClickListener which calls openDateRangePicker.
     */
    private fun handleScanDateStartEnd() {
        scanDateStart.setOnClickListener {
            openDateRangePicker()
        }

        scanDateEnd.setOnClickListener {
            openDateRangePicker()
        }
    }

    /**
     * Handles closing the dialog.
     */
    private fun handleClosingDialog() {
        val closeDialog = findViewById<TextView>(R.id.filter_receipts_dialog_close)
        closeDialog.setOnClickListener {
            receiptsFilterDialogListener?.onReturnedFilterOptions(filterOptions)
            this.dismiss()
        }
    }


    private fun loadFilters() {
        //Update the filters to the previously selected ones
        this.filterOptions = previousFilterOptions

        //Merchant
        if (filterOptions.merchantName.isNotEmpty()) {
            merchantChip.visibility = View.VISIBLE
            merchantOptions.setText(filterOptions.merchantName)
        }
        //Location
        if (filterOptions.location.isNotEmpty()) {
            locationChip.visibility = View.VISIBLE
            locationOptions.setText(filterOptions.location)
        }
        //Currency
        if (filterOptions.currency.isNotEmpty()) {
            currencyChip.visibility = View.VISIBLE
            currencyOptions.setText(filterOptions.currency)
        }
        //Coupon
        if (filterOptions.coupon.isNotEmpty()) {
            couponChip.visibility = View.VISIBLE
            couponOptions.setText(filterOptions.coupon)
        }
        //Total
        if (filterOptions.total.isNotEmpty()) {
            totalChip.visibility = View.VISIBLE
            totalOptions.setText(filterOptions.total)
        }
        //Date
        if (filterOptions.scanDateStart.isNotEmpty() && filterOptions.scanDateEnd.isNotEmpty()) {
            dateChip.visibility = View.VISIBLE
            scanDateStart.setText(filterOptions.scanDateStart)
            scanDateEnd.setText(filterOptions.scanDateEnd)
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
        filterOptions.scanDateStart = getDateFromMilliseconds(start)
        filterOptions.scanDateEnd = getDateFromMilliseconds(end)
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
            scanDateStart.setText("")
            scanDateEnd.setText("")
        } else {
            //Update start date
            scanDateStart.setText(getDateFromMilliseconds(start))

            //update end date
            scanDateEnd.setText(getDateFromMilliseconds(end))
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
                    totalChip = chip
                }
                5 -> {
                    dateChip = chip
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
                merchantOptions.text.clear()
            }
            1 -> {
                locationChip.isChecked = true
                filterOptions.location = ""
                locationOptions.text.clear()
            }
            2 -> {
                currencyChip.isChecked = true
                filterOptions.currency = ""
                currencyOptions.text.clear()
            }
            3 -> {
                couponChip.isChecked = true
                filterOptions.coupon = ""
                couponOptions.text.clear()
            }
            4 -> {
                totalChip.isChecked = true
                filterOptions.total = ""
                totalOptions.text.clear()
            }
            5 -> {
                filterOptions.scanDateStart = ""
                filterOptions.scanDateEnd = ""
                dateChip.visibility = View.GONE
                scanDateStart.setText("")
                scanDateEnd.setText("")
            }
        }
    }

    /**
     * Handles merchant filter.
     */
    private fun handleMerchants(merchants: List<String>) {
        val merchantReceipts: MutableList<String> = merchants.distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, merchantReceipts)
        merchantOptions.setAdapter(adapter)
        merchantOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.merchantName = ""
            merchantChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.merchantName = value
                merchantChip.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles location filter.
     */
    private fun handleLocations(locations: List<String>) {
        val locationReceipts: MutableList<String> = locations.distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, locationReceipts)
        locationOptions.setAdapter(adapter)
        locationOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.location = ""
            locationChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.location = value
                locationChip.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles currency filter.
     */
    private fun handleCurrencies(currencies: List<String>) {
        val currencyReceipts: MutableList<String> = currencies.distinct()
            .sortedBy { it.lowercase() } as MutableList<String>

        val adapter = ArrayAdapter(context, R.layout.list_receipts, currencyReceipts)
        currencyOptions.setAdapter(adapter)
        currencyOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.currency = ""
            currencyChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.currency = value
                currencyChip.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles coupon filter.
     */
    private fun handleCoupons(coupons: List<Double>) {
        val couponReceipts: List<String> = coupons.distinct()
            .sortedBy { it }.map { it.toString() }

        val adapter = ArrayAdapter(context, R.layout.list_receipts, couponReceipts)
        couponOptions.setAdapter(adapter)
        couponOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.coupon = ""
            couponChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.coupon = value
                couponChip.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Handles total filter.
     */
    private fun handleTotals(totals: List<Double>) {
        val totalReceipts: List<String> = totals.distinct()
            .sortedBy { it }.map { it.toString() }

        val adapter = ArrayAdapter(context, R.layout.list_receipts, totalReceipts)
        totalOptions.setAdapter(adapter)
        totalOptions.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            filterOptions.total = ""
            totalChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.total = value
                totalChip.visibility = View.VISIBLE
            }
        }
    }
}