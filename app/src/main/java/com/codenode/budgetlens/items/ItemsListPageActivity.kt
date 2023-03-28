package com.codenode.budgetlens.items

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Categories
import com.codenode.budgetlens.data.Items
import com.codenode.budgetlens.data.UserItems.Companion.requestItemsFromAPI
import com.codenode.budgetlens.data.UserItems.Companion.pageNumber
import com.codenode.budgetlens.items.filter.ItemsFilterDialog
import com.codenode.budgetlens.items.filter.ItemsFilterDialogListener
import com.codenode.budgetlens.items.filter.ItemsFilterOptions
import com.codenode.budgetlens.items.sort.ItemsSortDialog
import com.codenode.budgetlens.items.sort.ItemsSortDialogListener
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity
import com.codenode.budgetlens.utils.HttpResponseListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.util.*

class ItemsListPageActivity : AppCompatActivity(), ItemsSortDialogListener,
    ItemsFilterDialogListener, HttpResponseListener {
    private var pageSize = 5
    private var itemsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemsAdapter: ItemsRecyclerViewAdapter
    private val sortOptions = SortOptions()
    private var filterOptions = ItemsFilterOptions()
    private var isFromSingleReceipt = false
    var searchQuery = ""
    var queryParams = ""
    var userCategories = mutableListOf<Categories>()
    private lateinit var chipGroup: ChipGroup
    private var receiptID = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)
        CommonComponents.handleScanningReceipts(this.window.decorView, this, ActivityName.ITEMS)

        isFromSingleReceipt = intent.getBooleanExtra("singleReceiptView", false)
        receiptID = intent.getIntExtra("receiptID", -1)

        handleChipGroup()
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
            val dialog = ItemsSortDialog(this, R.style.ItemSortDialog, sortOptions)
            dialog.show()
        }
    }

    private fun handleChipGroup() {
        //get the starred category lists from api
        chipGroup = findViewById(R.id.category_chips)
        runOnUiThread {
            addChip("All", 0, R.style.AllChipStyle)
        }
        val url =
            "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/?category_toggle_star=true"

        val itemsRequest = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .addHeader("Content-Type", "application/json")
            .build()
        itemsRequest.newCall(request).enqueue(object : Callback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()

                        if (responseBody != null) {
                            val starredCategories = JSONArray(responseBody)

                            for (i in 0 until starredCategories.length()) {
                                val category = starredCategories.getJSONObject(i)
                                val id = category.getInt("id")
                                val name = category.getString("category_name")
                                userCategories.add(Categories(id, name))
                                runOnUiThread {
                                    addChip(name, id, R.style.ItemSortChipStyle)
                                }
                            }

                            Log.i("Successful", "Successfully loaded chips from API.")
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

            override fun onFailure(call: Call, e: IOException) {
                Log.i("ItemListPage - Chips.", chipGroup.toString() + "\r\n ${e.printStackTrace()}")
            }
        })
    }

    /**
     * Handles the filter.
     */
    private fun handleFilter() {
        val filterButton = findViewById<Button>(R.id.filter_item_btn_open)
        filterButton.setOnClickListener {
            val dialog = ItemsFilterDialog(
                this,
                R.style.fullscreendialog,
                supportFragmentManager,
                filterOptions,
                isFromSingleReceipt
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
        itemsListRecyclerView = findViewById(R.id.items_list)
        queryParams = generateQueryParams()

        requestItemsFromAPI(VIEW_ITEMS_FIRST_LOAD, this, pageSize, queryParams)


        //set item list recycler view
        if (itemsListRecyclerView != null) {
            itemsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            itemsListRecyclerView!!.layoutManager = linearLayoutManager
            itemsAdapter = ItemsRecyclerViewAdapter(context)

            itemsListRecyclerView!!.adapter = itemsAdapter
            itemsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //request receipts
                        requestItemsFromAPI(
                            VIEW_ITEMS_SCROLL_STATE_CHANGE,
                            context,
                            pageSize,
                            queryParams
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

                override fun onQueryTextChange(newText: String?): Boolean {
                    //Since we are fetching items with new parameters, reset the page number to 1
                    pageNumber = 1
                    searchQuery = newText.toString()

                    queryParams = generateQueryParams()
                    println(queryParams)
                    requestItemsFromAPI(
                        ReceiptsListPageActivity.VIEW_ITEMS_SEARCH,
                        context,
                        pageSize,
                        queryParams
                    )
                    return true
                }
            })
        }
    }

    /**
     * Handles when an item request http request comes back successfully.
     *
     * VIEW_ITEMS_FIRST_LOAD - triggered by first opening the items activity and initiating the fist load
     * VIEW_ITEMS_SCROLL_STATE_CHANGE - triggered by loading new items when dragging finger pointer down
     * VIEW_ITEM_FILTER - triggered when a filter is set
     * VIEW_ITEMS_SEARCH - triggered when user inputs in search view
     */
    override fun onHttpSuccess(
        viewItemRequestType: Int,
        mutableList: MutableList<*>,
        totalPrice: Double
    ) {
        Log.i(
            "Items-OnHttpSuccess",
            "An Http request triggered by type $viewItemRequestType was successful."
        )
        val itemList = (mutableList as MutableList<Items>).map { it.copy() }.toMutableList()

        //Update the adapter items
        runOnUiThread {
            when (viewItemRequestType) {
                VIEW_ITEMS_FIRST_LOAD,
                VIEW_ITEMS_SCROLL_STATE_CHANGE -> {
                    itemsAdapter.addItems(
                        itemList,
                        ::applyItemSortOptions,
                        totalPrice
                    )
                }
                VIEW_ITEM_FILTER -> {
                    itemsAdapter.addFilteredItems(
                        itemList,
                        ::applyItemSortOptions,
                        totalPrice
                    )
                }
                VIEW_ITEMS_SEARCH -> {
                    itemsAdapter.addSearchedItems(
                        itemList,
                        ::applyItemSortOptions,
                        totalPrice
                    )
                }
                else -> {
                    Log.i(
                        "Items-OnHttpSuccess",
                        "Unknown viewItemRequestType detected: $viewItemRequestType"
                    )
                }
            }
        }
    }

    /**
     * Handles when an http request comes back with an error.
     */
    override fun onHttpError() {
        Log.i("Items-OnHttpError", "An Http error was returned.")
    }

    class SortOptions {
        var isPriceAscending = false
        var isPriceDescending = false
        var isNameAscending = false
        var isNameDescending = false

        override fun toString(): String {
            return "SortOptions(isPriceAscending=$isPriceAscending, isPriceDescending=$isPriceDescending, isNameAscending=$isNameAscending, isNameDescending=$isNameDescending)"
        }

        fun isSortingEnabled(): Boolean {
            return isPriceAscending || isPriceDescending || isNameAscending || isNameDescending
        }
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

        runOnUiThread {
            //Revert the previous sort
            itemsAdapter.revertAppliedSort()
            // Sort them with the new options
            val sortedReceiptList =
                applyItemSortOptions(itemsAdapter.getUnsortedItems())
            itemsAdapter.sortItems(sortedReceiptList)
        }
    }

    /**
     * Sorts the itemList MutableList<Items> based on sortOptions from user and returns it.
     */
    private fun applyItemSortOptions(itemList: MutableList<Items>): MutableList<Items> {
        if (sortOptions.isSortingEnabled()) {
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
        return itemList
    }

    /**
     * A listener that gets back the filters set in the ItemFilterDialog and applies them.
     */
    override fun onReturnedFilterOptions(newFilterOptions: ItemsFilterOptions) {
        this.filterOptions = newFilterOptions

        //Generate the new queryParams and assign them
        queryParams = generateQueryParams()

        //We need to set the page number back to 1 when changing the entire dataset
        pageNumber = 1

        //request
        requestItemsFromAPI(VIEW_ITEM_FILTER, this, pageSize, queryParams)
    }

    /**
     * Handles results from opened activities by this activity.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_INFO_ACTIVITY) {
            val posToRemove = data?.getIntExtra("position", -1)
            val price = data?.getDoubleExtra("price", 0.0)
            if (posToRemove != null && posToRemove != -1 && price != null) {
                itemsAdapter.removeItem(posToRemove, -price)
            }
        }
    }

    /**
     * Handles opening the single item page using this activity.
     */
    fun openItemInfoActivity(item: Items, position: Int) {
        //save the itemId and position for the item info page
        val intent = Intent(this, ItemInfoActivity::class.java)
        intent.putExtra("itemId", item.id.toString())
        intent.putExtra("position", position)
        startActivityForResult(intent, ITEM_INFO_ACTIVITY)
    }

    private fun addChip(label: String, id: Int, styleRes: Int) {
        val chip = Chip(this)
        val chipDrawable = ChipDrawable.createFromAttributes(this, null, 0, styleRes)
        chip.text = label
        if (chip.text == "All") {
            chip.setTextColor(Color.WHITE)
        }
        chip.isClickable = true
        chip.setChipDrawable(chipDrawable)
        chip.setOnClickListener {
            if (id != 0 && chip.isChecked) {
                filterOptions.categoryName = label
                filterOptions.categoryId = id
            } else {
                filterOptions.categoryName = ""
                filterOptions.categoryId = -1
            }
            queryParams = generateQueryParams()

            //We need to set the page number back to 1 when changing the entire dataset
            pageNumber = 1

            //request
            requestItemsFromAPI(VIEW_ITEM_FILTER, this, pageSize, queryParams)
        }
        chipGroup.addView(chip)
    }

    @SuppressLint("ResourceType")
    fun generateQueryParams(): String {
        val queryParams = ArrayList<String>()

        val sb = StringBuilder("?")

        if (isFromSingleReceipt) {
            if (receiptID == -1) {
                Log.i("ItemsListPageActivity,", "Tried to open activity with -1 receiptID. ")
            } else {
                queryParams.add("receipt=${receiptID}")
            }
        }
        if (filterOptions.categoryName.isNotEmpty() && filterOptions.categoryId > -1) {
            queryParams.add("category_id=${filterOptions.categoryId}")
            var id = -1
            for (i in 0 until userCategories.size) {
                if (userCategories[i].category_name == filterOptions.categoryName) {
                    id = i
                }
            }
            chipGroup.check(id + 2)
        } else {
            chipGroup.check(1)
        }

        if (filterOptions.merchantName.isNotEmpty() && filterOptions.merchantId > -1) {
            queryParams.add("merchant_id=${filterOptions.merchantId}")
        }
        if (filterOptions.startDate.isNotEmpty() && filterOptions.endDate.isNotEmpty()) {
            queryParams.add(
                "start_date=${filterOptions.startDate}&end_date=${filterOptions.endDate}"
            )
        }
        if (filterOptions.minPrice > 0 && filterOptions.maxPrice > 0) {
            queryParams.add("min_price=${filterOptions.minPrice}&max_price=${filterOptions.maxPrice}")
        }
        if (searchQuery.isNotEmpty()) {
            queryParams.add("search=$searchQuery")
        }
        for (i in 0 until queryParams.size) {
            if (i == 0) {
                sb.append(queryParams[i])
            } else {
                sb.append("&${queryParams[i]}")
            }
        }

        return if (queryParams.isEmpty()) "" else sb.toString()
    }

    companion object {
        const val ITEM_INFO_ACTIVITY = 6463646
        const val VIEW_ITEMS_FIRST_LOAD = 0
        const val VIEW_ITEMS_SCROLL_STATE_CHANGE = 1
        const val VIEW_ITEM_FILTER = 3
        const val VIEW_ITEMS_SEARCH = 4
    }
}
