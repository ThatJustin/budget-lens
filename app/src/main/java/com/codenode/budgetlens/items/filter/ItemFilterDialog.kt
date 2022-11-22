package com.codenode.budgetlens.items.filter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.codenode.budgetlens.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ItemFilterDialog(
    private val activityContext: Context,
    themeID: Int,
    private val supportFragmentManager: FragmentManager,
    private val previousFilterOptions: ItemFilterOptions
) : Dialog(activityContext, themeID) {

    private val calendar = Calendar.getInstance()
    private val dateFormatString = "yyyy/MM/dd"
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

    private var itemFilterDialogListener: ItemFilterDialogListener? = null
    var filterOptions = ItemFilterOptions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView: View = layoutInflater.inflate(R.layout.item_filter_dialog, null)
        setContentView(dialogView)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //Set listener
        itemFilterDialogListener = activityContext as Activity as ItemFilterDialogListener

        //Active filters
        activeFilters = findViewById(R.id.active_item_filters_chip_group)

        // Merchant
        merchantOptions = findViewById(R.id.item_filter_merchant_options)

        //Category
        categoryOptions = findViewById(R.id.item_filter_category_options)

        //Date
        startDate = findViewById(R.id.item_filter_start_date)
        endDate = findViewById(R.id.item_filter_end_date)

        //Price
        minPrice = findViewById(R.id.item_filter_min_price)
        maxPrice = findViewById(R.id.item_filter_max_price)

        handleClosingDialog()
        handleChipClicking()
        handleMerchant()
        handleCategory()
        handleStartEndDate()
        handlePriceRange()
        loadFilters()
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
            merchantOptions.setText(filterOptions.merchantName)
        }
        //Category
        if (filterOptions.categoryName.isNotEmpty()) {
            categoryChip.visibility = View.VISIBLE
            categoryOptions.setText(filterOptions.categoryName)
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
     * Updates the start and end date text fields, their visibility and
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
                merchantOptions.text.clear()
            }
            1 -> {
                categoryChip.isChecked = true
                filterOptions.categoryName = ""
                categoryOptions.text.clear()
            }
            2 -> {
                filterOptions.startDate = 0
                filterOptions.endDate = 0
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
        //TODO load merchants and remove duplicates
        val items = listOf(
            "",
            "BestBuy",
            "Future Shop",
        ).sortedBy { it.lowercase() }
        val adapter = ArrayAdapter(context, R.layout.list_item, items)
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
     * Handles category filter.
     */
    private fun handleCategory() {
        //TODO load category and remove duplicates
        val items = listOf(
            "",
            "cat 1",
            "cat 2"
        ).sortedBy { it.lowercase() }
        val adapter = ArrayAdapter(context, R.layout.list_item, items)
        categoryOptions.setAdapter(adapter)
        categoryOptions.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            filterOptions.categoryName = ""
            categoryChip.visibility = View.GONE
            val value = adapter.getItem(pos) ?: ""
            if (value.isNotEmpty()) {
                filterOptions.categoryName = value
                categoryChip.visibility = View.VISIBLE
            }
        }
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
        val closeDialog = findViewById<ImageButton>(R.id.filter_item_dialog_close)
        closeDialog.setOnClickListener {
            itemFilterDialogListener?.onReturnedFilterOptions(filterOptions)
            this.dismiss()
        }
    }
}