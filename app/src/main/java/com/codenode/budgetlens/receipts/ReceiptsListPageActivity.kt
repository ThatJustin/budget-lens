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
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity.ReceiptsListPageActivity.pageSize
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterDialog
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterDialogListener
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterOptions
import com.codenode.budgetlens.receipts.sort.ReceiptsSortDialog
import com.codenode.budgetlens.receipts.sort.ReceiptsSortDialogListener

class ReceiptsListPageActivity : AppCompatActivity(), ReceiptsSortDialogListener,
    ReceiptsFilterDialogListener {

    object ReceiptsListPageActivity {
        var pageSize = 5
    }

    private lateinit var receiptsList: MutableList<Receipts>
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var receiptsAdapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>
    private val sortOptions = SortOptions()
    private var filterOptions = ReceiptsFilterOptions()
    var additionalData = ""

    //Save an untouched copy for when sorting/filtering is undone
    private lateinit var receiptsListUntouched: MutableList<Receipts>

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
                    progressBar.visibility = View.VISIBLE
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //Before loading, revert to the old order
                        receiptsList.clear()
                        receiptsList.addAll(receiptsListUntouched)

                        //Load in more
                        receiptsList = loadReceiptsFromAPI(context, pageSize, additionalData)

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
        var isLocationAscending = false
        var isLocationDescending = false
        var isCouponAscending = false
        var isCouponDescending = false
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
        isLocationAscending: Boolean,
        isLocationDescending: Boolean,
        isCouponAscending: Boolean,
        isCouponDescending: Boolean,
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
        sortOptions.isLocationAscending = isLocationAscending
        sortOptions.isLocationDescending = isLocationDescending
        sortOptions.isCouponAscending = isCouponAscending
        sortOptions.isCouponDescending = isCouponDescending
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
        if (sortOptions.isLocationAscending) {
            receiptsList.sortBy { it.location }
        }
        if (sortOptions.isLocationDescending) {
            receiptsList.sortByDescending { it.location }
        }
        if (sortOptions.isCouponAscending) {
            receiptsList.sortBy { it.coupon }
        }
        if (sortOptions.isCouponDescending) {
            receiptsList.sortByDescending { it.coupon }
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

        val filterOptionList = ArrayList<String>()
        val sb = StringBuilder("?")
        additionalData = ""

        //set additionalData here
        if (filterOptions.merchantName.isNotEmpty()) {
            filterOptionList.add("merchant_name=${filterOptions.merchantName}")
        }
        if (filterOptions.location.isNotEmpty()) {
            filterOptionList.add("location=${filterOptions.location}")
        }
        if (filterOptions.coupon.isNotEmpty()) {
            filterOptionList.add("coupon=${filterOptions.coupon}")
        }
        if (filterOptions.currency.isNotEmpty()) {
            filterOptionList.add("currency=${filterOptions.currency}")
        }
        if (filterOptions.total.isNotEmpty()) {
            filterOptionList.add("total=${filterOptions.total}")
        }
        if (filterOptions.scanDateStart.isNotEmpty() && filterOptions.scanDateEnd.isNotEmpty()) {
            filterOptionList.add("scan_date_start=${filterOptions.scanDateStart}&scan_date_end=${filterOptions.scanDateEnd}"
            )
        }

        for (i in 0 until filterOptionList.size) {
            if (i == 0) {
                sb.append(filterOptionList[i])
            } else {
                sb.append("&${filterOptionList[i]}")
            }
        }

        additionalData = sb.toString()
        println("additionalData $additionalData")

        receiptsList.clear()

        pageNumber = 1

        //reload
        receiptsList = loadReceiptsFromAPI(this, pageSize, additionalData)

        // update the untouched
        receiptsListUntouched = receiptsList.map { it.copy() }.toMutableList()

        //Apply whatever sort is set
        applyReceiptsSortOptions()

        //Update the adapter items
        receiptsAdapter.notifyDataSetChanged()
    }
}