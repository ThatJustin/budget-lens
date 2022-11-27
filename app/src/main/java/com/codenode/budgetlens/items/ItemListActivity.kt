package com.codenode.budgetlens.items

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Items
import com.codenode.budgetlens.data.UserItems.Companion.loadItemsFromAPI
import com.codenode.budgetlens.data.UserItems.Companion.pageNumber
import com.codenode.budgetlens.data.UserItems.Companion.userItems
import com.codenode.budgetlens.items.filter.ItemFilterDialog
import com.codenode.budgetlens.items.filter.ItemFilterDialogListener
import com.codenode.budgetlens.items.filter.ItemFilterOptions
import com.codenode.budgetlens.items.sort.ItemSortDialog
import com.codenode.budgetlens.items.sort.ItemSortDialogListener
import java.util.*


class ItemListActivity : AppCompatActivity(), ItemSortDialogListener, ItemFilterDialogListener {
    //Save an untouched copy for when sorting/filtering is undone
    private lateinit var itemListUntouched: MutableList<Items>

    private lateinit var itemList: MutableList<Items>
    private var itemsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemAdapter: RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    var additionalData = ""
    private val sortOptions = SortOptions()
    private var filterOptions = ItemFilterOptions()

    private lateinit var itemTotal: TextView
    private lateinit var result: Pair<MutableList<Items>, Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        val searchBar: SearchView = findViewById(R.id.search_bar_text)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)
        itemTotal = findViewById(R.id.item_cost_value)

        handleAdapter()
        handleSort()
        handleFilter()
    }

    /**
     * Handles the item sort showing.
     */
    private fun handleSort() {
        val sortButton = findViewById<Button>(R.id.sort_item_btn_open)
        sortButton.setOnClickListener {
            val dialog = ItemSortDialog(this, R.style.ItemSortDialog, sortOptions)
            dialog.show()
        }
    }

    /**
     * Handles the filter.
     */
    private fun handleFilter() {
        val filterButton = findViewById<Button>(R.id.filter_item_btn_open)
        filterButton.setOnClickListener {
            val dialog = ItemFilterDialog(
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
        userItems.clear()

        pageNumber = 1

        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        //load the list
        result = loadItemsFromAPI(this, pageSize, additionalData)
        itemList = result.first
        itemTotal.text = result.second.toString()
        itemListUntouched = itemList.map { it.copy() }.toMutableList()

        val context = this

        itemsListRecyclerView = findViewById(R.id.item_list)
        progressBar.visibility = View.VISIBLE

        if (itemList.isEmpty()) {
            itemsListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        if (itemsListRecyclerView != null) {
            itemsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            itemsListRecyclerView!!.layoutManager = linearLayoutManager
            itemAdapter = ItemsRecyclerViewAdapter(itemList, this)

            itemsListRecyclerView!!.adapter = itemAdapter
            progressBar.visibility = View.GONE
            itemsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //Before loading, revert to the old order
                        itemList.clear()
                        itemList.addAll(itemListUntouched)

                        //Load in more
                        result = loadItemsFromAPI(context, pageSize, additionalData)
                        itemList = result.first
                        itemTotal.text = result.second.toString()

                        // update the untouched
                        itemListUntouched = itemList.map { it.copy() }.toMutableList()

                        //Apply whatever sort is set
                        applyItemSortOptions()

                        //Update the adapter items
                        itemAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE
                }
            }
            )
        }
    }

    class SortOptions {
        var isPriceAscending = false
        var isPriceDescending = false
        var isNameAscending = false
        var isNameDescending = false

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onReturnedSortOptions(
        isPriceAscending: Boolean,
        isPriceDescending: Boolean,
        isNameAscending: Boolean,
        isNameDescending: Boolean
    ) {
        //Update the options
        sortOptions.isPriceAscending = isPriceAscending
        sortOptions.isPriceDescending = isPriceDescending
        sortOptions.isNameAscending = isNameAscending
        sortOptions.isNameDescending = isNameDescending

        applyItemSortOptions()

        //Update adapter of changes
        itemAdapter.notifyDataSetChanged()
    }

    /**
     * Sorts the itemList MutableList<Items> based on sortOptions from user.
     */
    private fun applyItemSortOptions() {
        // Restore itemList to the untouched state

        itemList.clear()
        itemList.addAll(itemListUntouched)

        //Sort
        if (sortOptions.isPriceAscending) {
            itemList.sortBy { it.price }
        }
        if (sortOptions.isPriceDescending) {
            itemList.sortByDescending { it.price }
        }
        if (sortOptions.isNameAscending) {
            itemList.sortBy { it.name.lowercase() }
        }
        if (sortOptions.isNameDescending) {
            itemList.sortByDescending { it.name.lowercase() }
        }
    }

    /**
     * A listener that gets back the filters set in the ItemFilterDialog.
     */
    override fun onReturnedFilterOptions(newFilterOptions: ItemFilterOptions) {
        this.filterOptions = newFilterOptions
        var sb = StringBuilder()
        additionalData = ""
        println("categoryName " + filterOptions.categoryName)
        println("categoryId " + filterOptions.categoryId)
        println(" merchantName" + filterOptions.merchantName)
        println("startDate " + filterOptions.startDate)
        println(" endDate" + filterOptions.endDate)
        println("maxPrice " + filterOptions.maxPrice)
        println(" minPrice" + filterOptions.minPrice)
        //set additionalData here
        if (filterOptions.categoryName.isNotEmpty() && filterOptions.categoryId > -1) {
            sb.append("?category_id=${filterOptions.categoryId}")
        }
//        if (filterOptions.categoryName.isNotEmpty() && filterOptions.merchantId > -1) {
//            sb.append("?merchant_id=${filterOptions.merchantId}")
//        }

        if (filterOptions.startDate > 0 && filterOptions.endDate > 0) {

        }
        if (filterOptions.maxPrice > 0 && filterOptions.minPrice > 0) {

        }
        additionalData = sb.toString()

        itemList.clear()
        itemList.addAll(itemListUntouched)

        //Load in more
        result = loadItemsFromAPI(this, pageSize, additionalData)
        itemList = result.first
        itemTotal.text = result.second.toString()

        // update the untouched
        itemListUntouched = itemList.map { it.copy() }.toMutableList()

        //Apply whatever sort is set
        applyItemSortOptions()

        //Update the adapter items
        itemAdapter.notifyDataSetChanged()
        // update adapter
    }
}