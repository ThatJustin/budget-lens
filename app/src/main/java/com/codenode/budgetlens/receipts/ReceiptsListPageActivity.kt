package com.codenode.budgetlens.receipts

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.budget.BudgetPageActivity
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Receipts
import com.codenode.budgetlens.data.UserReceipts.Companion.requestReceiptsFromAPI
import com.codenode.budgetlens.data.UserReceipts.Companion.pageNumber
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity.ReceiptsListPageActivity.pageSize
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterDialog
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterDialogListener
import com.codenode.budgetlens.receipts.filter.ReceiptsFilterOptions
import com.codenode.budgetlens.receipts.sort.ReceiptsSortDialog
import com.codenode.budgetlens.receipts.sort.ReceiptsSortDialogListener
import com.codenode.budgetlens.utils.HttpResponseListener
import kotlinx.android.synthetic.main.activity_receipts_list_page.*

class ReceiptsListPageActivity : AppCompatActivity(), ReceiptsSortDialogListener,
    ReceiptsFilterDialogListener, HttpResponseListener {

    object ReceiptsListPageActivity {
        var pageSize = 5
    }

    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var receiptsAdapter: ReceiptsRecyclerViewAdapter
    private val sortOptions = SortOptions()
    private var filterOptions = ReceiptsFilterOptions()
    var additionalData = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        handleAdapter()
        handleSort()
        handleFilter()
        fabut.setOnClickListener {
            val intent = Intent(this, BudgetPageActivity::class.java)
            this.overridePendingTransition(0, 0)
            this.finish()
            startActivity(intent)
        }
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
        pageNumber = 1

        val context = this
        val searchBar: SearchView = findViewById(R.id.search_bar_text)
        receiptsListRecyclerView = findViewById(R.id.receipts_list)

        //request receipts from backend
        requestReceiptsFromAPI(VIEW_ITEMS_FIRST_LOAD, this, pageSize, additionalData)

        if (receiptsListRecyclerView != null) {
            receiptsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            receiptsListRecyclerView!!.layoutManager = linearLayoutManager
            receiptsAdapter = ReceiptsRecyclerViewAdapter()
            receiptsListRecyclerView!!.adapter = receiptsAdapter

            receiptsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //request receipts
                        requestReceiptsFromAPI(
                            VIEW_ITEMS_SCROLL_STATE_CHANGE,
                            context,
                            pageSize,
                            additionalData
                        )
                    }
                }
            })

            //listener for search bar input
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //No need to do anything here
                    return true
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    //Since we are fetching items with new parameters, reset the page number to 1
                    pageNumber = 1
                    requestReceiptsFromAPI(VIEW_ITEMS_SEARCH, context, pageSize, "?search=$newText")
                    return true
                }
            })
        }
    }

    override fun onHttpSuccess(viewItemRequestType: Int, mutableList: MutableList<*>) {
        Log.i(
            "Receipts-OnHttpSuccess",
            "An Http request triggered by type $viewItemRequestType was successful."
        )
        val receiptsList = (mutableList as MutableList<Receipts>).map { it.copy() }.toMutableList()


        //Update the adapter items
        runOnUiThread {
            //Apply whatever sort is set
            applyReceiptsSortOptions()
            receiptsAdapter.changeDataSet(receiptsList, viewItemRequestType)
        }
    }

    override fun onHttpError() {
        Log.i("Receipts-OnHttpError", "An Http error was returned.")
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

        override fun toString(): String {
            return "SortOptions(isMerchantAscending=$isMerchantAscending, isMerchantDescending=$isMerchantDescending, isLocationAscending=$isLocationAscending, isLocationDescending=$isLocationDescending, isCouponAscending=$isCouponAscending, isCouponDescending=$isCouponDescending, isTaxAscending=$isTaxAscending, isTaxDescending=$isTaxDescending, isTipAscending=$isTipAscending, isTipDescending=$isTipDescending, isTotalAscending=$isTotalAscending, isTotalDescending=$isTotalDescending)"
        }
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

        runOnUiThread {
            applyReceiptsSortOptions()
        }
    }

    /**
     * Sorts the receiptsList MutableList<Receipts> based on sortOptions from user.
     */
    private fun applyReceiptsSortOptions() {
        // Restore receiptsList to the untouched state
        receiptsAdapter.revertAppliedSort()

        // Sort
        //Here we grab the clean receipts and make a deep copy using toMutableList
        //this is to prevent no items from being shown when applySort calls setReceipts which clears (same reference is cleared)
        val receiptsList = receiptsAdapter.getReceipts().toMutableList()
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

        //Apply the sort
        receiptsAdapter.applySort(receiptsList)
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
            filterOptionList.add(
                "scan_date_start=${filterOptions.scanDateStart}&scan_date_end=${filterOptions.scanDateEnd}"
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

        //We need to set the page number back to 1 when changing the entire dataset
        pageNumber = 1

        //request
        requestReceiptsFromAPI(VIEW_ITEM_FILTER, this, pageSize, additionalData)
    }

    companion object {
        val VIEW_ITEMS_FIRST_LOAD = 0
        val VIEW_ITEMS_SCROLL_STATE_CHANGE = 1
        val VIEW_ITEM_FILTER = 3
        val VIEW_ITEMS_SEARCH = 4
    }
}