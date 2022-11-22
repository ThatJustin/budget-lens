package com.codenode.budgetlens.items.filter

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
    context: Context,
    themeID: Int,
    private val supportFragmentManager: FragmentManager
) : Dialog(context, themeID) {
    private val calendar = Calendar.getInstance()
    private val dateFormatString = "yyyy/MM/dd"
    var filterOptions = ItemFilterOptions()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView: View = layoutInflater.inflate(R.layout.item_filter_dialog, null)
        setContentView(dialogView)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

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
        handleDateRange()
        handlePriceRange()

    }

    private fun openDatePicker() {
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

    private fun updateDateSelection(first: Long, second: Long, visible: Int) {
        filterOptions.startDate = first
        filterOptions.endDate = second
        dateChip.visibility = visible
        updateDateTextView(first, second)
    }

    /**
     * Updates the start date and end date text views with the input millisecond timestamp.
     */
    private fun updateDateTextView(first: Long, second: Long) {
        if (first == 0L && second == 0L) { /// user did not save the selected date
            startDate.setText("")
            endDate.setText("")
        } else {
            //Update start date
            calendar.timeInMillis = first

            var dateFormat = SimpleDateFormat(dateFormatString, Locale.CANADA)
            startDate.setText(dateFormat.format(calendar.time))

            //update end date
            calendar.timeInMillis = second
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

    private fun handleMerchant() {
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

    private fun handleCategory() {
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

    private fun handleDateRange() {
        startDate.setOnClickListener {
            openDatePicker()
        }

        maxPrice.setOnClickListener {
            openDatePicker()
        }
    }


    /**
     * Handles the min and max price inputs.
     * Detects when user enters valid prices and shows/hides the price chip when needed.
     */
    private fun handlePriceRange() {

        fun showPriceChip() {
            if (isMinPriceSet && isMaxPriceSet) {
                priceChip.visibility = View.VISIBLE
            } else {
                priceChip.visibility = View.GONE
            }
        }

        //Author https://stackoverflow.com/a/24397810
        fun ensureValidDecimalPosition(s: String, maxDecimalDigits: Int): String {
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
                showPriceChip()
            }

            //Author https://stackoverflow.com/a/24397810
            override fun afterTextChanged(s: Editable) {
                val str: String = s.toString()
                if (str.isEmpty()) return
                val str2 = ensureValidDecimalPosition(str, 2)
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
                showPriceChip()
            }

            //Author https://stackoverflow.com/a/24397810
            override fun afterTextChanged(s: Editable) {
                val str: String = s.toString()
                if (str.isEmpty()) return
                val str2 = ensureValidDecimalPosition(str, 2)
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

    private fun handleClosingDialog() {
        val closeDialog = findViewById<ImageButton>(R.id.filter_item_dialog_close)
        closeDialog.setOnClickListener {
            this.dismiss()
        }
    }
}