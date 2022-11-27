package com.codenode.budgetlens.receipts

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Receipts
import com.codenode.budgetlens.data.UserReceipts.Companion.loadReceiptsFromAPI
import com.codenode.budgetlens.data.UserReceipts.Companion.pageNumber
import com.codenode.budgetlens.data.UserReceipts.Companion.userReceipts
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterDialog
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterDialogListener
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterOptions
import com.codenode.budgetlens.receipts.sort.ReceiptsSortDialog
import com.codenode.budgetlens.receipts.sort.ReceiptsSortDialogListener

class ReceiptsListPageActivity : AppCompatActivity(), ReceiptsSortDialogListener,
    ReceiptsFilterDialogListener {
    private lateinit var receiptsList: MutableList<Receipts>
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var receiptsAdapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>
    private val sortOptions = SortOptions()
    private var filterOptions = ReceiptsFilterOptions()
    var additionalData = ""

    //Save an untouched copy for when sorting/filtering is undone
    private lateinit var receiptsListUntouched: MutableList<Receipts>

    val pageSize = 5

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        handleAdapter()
        handleSort()
        handleFilter()
    }

    /**
     * Handles the receipts sort showing.
     */
    private fun handleSort() {
        val sortButton = findViewById<Button>(R.id.sort_by_button)
        sortButton.setOnClickListener {
            val dialog = ReceiptsSortDialog(this, R.style.ReceiptsSortDialog, sortOptions)
            dialog.show()
        }
    }

    /**
     * Handles the filter.
     */
    private fun handleFilter() {
        val filterButton = findViewById<Button>(R.id.filter_button)
        filterButton.setOnClickListener {
            val dialog = ReceiptsFilterDialog(
                this,
                R.style.fullscreendialog,
                supportFragmentManager,
                filterOptions
            )
            dialog.show()
        }
    }

    /**
     * Handles RecycleView adapter.
     */
    private fun handleAdapter() {
        userReceipts.clear()
        pageNumber = 1

        val searchBar: SearchView = findViewById(R.id.search_bar_text)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        //load the list
        receiptsList = loadReceiptsFromAPI(this, pageSize, additionalData)
        receiptsListUntouched = receiptsList.map { it.copy() }.toMutableList()

        val context = this
        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        progressBar.visibility = View.VISIBLE
        if (receiptsList.isEmpty()) {
            receiptsListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
        if (receiptsListRecyclerView != null) {
            receiptsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            receiptsListRecyclerView!!.layoutManager = linearLayoutManager
            receiptsAdapter = ReceiptsRecyclerViewAdapter(receiptsList)
            receiptsListRecyclerView!!.adapter = receiptsAdapter
            progressBar.visibility = View.GONE
            receiptsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //Before loading, revert to the old order
                        receiptsList.clear()
                        receiptsList.addAll(receiptsListUntouched)

                        //Load in more
                        loadReceiptsFromAPI(context, pageSize, additionalData)

                        // update the untouched
                        receiptsListUntouched = receiptsList.map { it.copy() }.toMutableList()

                        //Apply whatever sort is set
                        applyReceiptsSortOptions()

                        //Update the adapter items
                        receiptsAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE
                }
            })

            //listener for search bar input
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //clean the data, otherwise the search will based on the previous search
                    additionalData = ""
                    receiptsList = loadReceiptsFromAPI(context, pageSize, additionalData)
                    receiptsAdapter.notifyDataSetChanged()

                    //perform the search
                    additionalData += "?search=" + searchBar.query
                    userReceipts.clear()
                    receiptsList = loadReceiptsFromAPI(context, pageSize, additionalData)
                    receiptsAdapter.notifyDataSetChanged()
                    return true
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    //clean the data, otherwise the search will based on the previous search
                    additionalData = ""
                    receiptsList = loadReceiptsFromAPI(context, pageSize, additionalData)
                    receiptsAdapter.notifyDataSetChanged()

                    //perform the search
                    additionalData += "?search=" + searchBar.query
                    userReceipts.clear()
                    receiptsList = loadReceiptsFromAPI(context, pageSize, additionalData)
                    receiptsAdapter.notifyDataSetChanged()
                    return true
                }
            })
        }
    }

    class SortOptions {
        var isMerchantAscending = false
        var isMerchantDescending = false
        var isCouponAscending = false
        var isCouponDescending = false
        var isLocationAscending = false
        var isLocationDescending = false
        var isTaxAscending = false
        var isTaxDescending = false
        var isTipAscending = false
        var isTipDescending = false
        var isTotalAscending = false
        var isTotalDescending = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onReturnedSortOptions(
        isMerchantAscending: Boolean,
        isMerchantDescending: Boolean,
        isCouponAscending: Boolean,
        isCouponDescending: Boolean,
        isLocationAscending: Boolean,
        isLocationDescending: Boolean,
        isTaxAscending: Boolean,
        isTaxDescending: Boolean,
        isTipAscending: Boolean,
        isTipDescending: Boolean,
        isTotalAscending: Boolean,
        isTotalDescending: Boolean
    ) {
        //Update the options
        sortOptions.isMerchantAscending = isMerchantAscending
        sortOptions.isMerchantDescending = isMerchantDescending
        sortOptions.isCouponAscending = isCouponAscending
        sortOptions.isCouponDescending = isCouponDescending
        sortOptions.isLocationAscending = isLocationAscending
        sortOptions.isLocationDescending = isLocationDescending
        sortOptions.isTaxAscending = isTaxAscending
        sortOptions.isTaxDescending = isTaxDescending
        sortOptions.isTipAscending = isTipAscending
        sortOptions.isTipDescending = isTipDescending
        sortOptions.isTotalAscending = isTotalAscending
        sortOptions.isTotalDescending = isTotalDescending

        applyReceiptsSortOptions()

        //Update adapter of changes
        receiptsAdapter.notifyDataSetChanged()
    }

    /**
     * Sorts the receiptsList MutableList<Receipts> based on sortOptions from user.
     */
    private fun applyReceiptsSortOptions() {
        // Restore receiptsList to the untouched state
        receiptsList.clear()
        receiptsList.addAll(receiptsListUntouched)

        //Sort
        if (sortOptions.isMerchantAscending) {
            receiptsList.sortBy { it.merchant_name }
        }
        if (sortOptions.isMerchantDescending) {
            receiptsList.sortByDescending { it.merchant_name }
        }
        if (sortOptions.isCouponAscending) {
            receiptsList.sortBy { it.coupon }
        }
        if (sortOptions.isCouponDescending) {
            receiptsList.sortByDescending { it.coupon }
        }
        if (sortOptions.isLocationAscending) {
            receiptsList.sortBy { it.location }
        }
        if (sortOptions.isLocationDescending) {
            receiptsList.sortByDescending { it.location }
        }
        if (sortOptions.isTaxAscending) {
            receiptsList.sortBy { it.tax }
        }
        if (sortOptions.isTaxDescending) {
            receiptsList.sortByDescending { it.tax }
        }
        if (sortOptions.isTipAscending) {
            receiptsList.sortBy { it.tip }
        }
        if (sortOptions.isTipDescending) {
            receiptsList.sortByDescending { it.tip }
        }
        if (sortOptions.isTotalAscending) {
            receiptsList.sortBy { it.total_amount }
        }
        if (sortOptions.isTotalDescending) {
            receiptsList.sortByDescending { it.total_amount }
        }
    }

    /**
     * A listener that gets back the filters set in the ReceiptsFilterDialog.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onReturnedFilterOptions(newFilterOptions: ReceiptsFilterOptions) {
        this.filterOptions = newFilterOptions

        val sb = StringBuilder()
        additionalData = ""

        println("merchantName " + filterOptions.merchantName)
        println("merchantId " + filterOptions.merchantId)
        println("coupon " + filterOptions.coupon)
        println("couponId " + filterOptions.couponId)
        println("location " + filterOptions.location)
        println("locationId " + filterOptions.locationId)
        println("tax " + filterOptions.tax)
        println("tip " + filterOptions.tip)
        println("startDate " + filterOptions.startDate)
        println("endDate " + filterOptions.endDate)
        println("maxPrice " + filterOptions.maxPrice)
        println("minPrice " + filterOptions.minPrice)

        //set additionalData here
        if (filterOptions.merchantName.isNotEmpty() && filterOptions.merchantId > -1) {
            sb.append("?merchant_id=${filterOptions.merchantId}")
        }
        if (filterOptions.location.isNotEmpty()) {
            sb.append("?location=${filterOptions.location}")
        }
        if (filterOptions.coupon.isNotEmpty() && filterOptions.couponId > -1) {
            sb.append("?coupon_id=${filterOptions.couponId}")
        }
        if (filterOptions.currency.isNotEmpty() && filterOptions.currencyId > -1) {
            sb.append("?currency_id=${filterOptions.currencyId}")
        }
//        if (filterOptions.tax.isFinite()) {
//            sb.append("?tax=${filterOptions.tax}")
//        }
//        if (filterOptions.tip.isFinite()) {
//            sb.append("?tip=${filterOptions.tip}")
//        }

//        // update adapter
//        if (filterOptions.startDate > 0 && filterOptions.endDate > 0) {
//            receiptsList.clear()
//            receiptsList.addAll(receiptsListUntouched)
//            receiptsList = receiptsList.filter {
//                it.scan_date >= filterOptions.startDate && it.scan_date <= filterOptions.endDate
//            } as MutableList<Receipts>
//            receiptsAdapter.notifyDataSetChanged()
//        }
//
//        if (filterOptions.maxPrice > 0 && filterOptions.minPrice > 0) {
//            receiptsList.clear()
//            receiptsList.addAll(receiptsListUntouched)
//            receiptsList = receiptsList.filter {
//                it.total_amount >= filterOptions.minPrice && it.total_amount <= filterOptions.maxPrice
//            } as MutableList<Receipts>
//            receiptsAdapter.notifyDataSetChanged()
//        }

        additionalData = sb.toString()
        println("additionalData $additionalData")

        receiptsList.clear()
        receiptsList.addAll(receiptsListUntouched)

        //Load in more
        receiptsList = loadReceiptsFromAPI(this, pageSize, additionalData)

        // update the untouched
        receiptsListUntouched = receiptsList.map { it.copy() }.toMutableList()

        //Apply whatever sort is set
        applyReceiptsSortOptions()

        //Update the adapter items
        receiptsAdapter.notifyDataSetChanged()
    }
}