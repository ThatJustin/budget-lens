package com.codenode.budgetlens.items

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Categories
import com.codenode.budgetlens.data.Items
import com.codenode.budgetlens.data.UserItems.Companion.loadItemsFromAPI
import com.codenode.budgetlens.data.UserItems.Companion.pageNumber
import com.codenode.budgetlens.data.UserItems.Companion.userItems
import com.codenode.budgetlens.items.filter.ItemsFilterDialog
import com.codenode.budgetlens.items.filter.ItemsFilterDialogListener
import com.codenode.budgetlens.items.filter.ItemsFilterOptions
import com.codenode.budgetlens.items.sort.ItemsSortDialog
import com.codenode.budgetlens.items.sort.ItemsSortDialogListener
import java.text.SimpleDateFormat
import java.util.*


class ItemsListPageActivity : AppCompatActivity(), ItemsSortDialogListener,
    ItemsFilterDialogListener {
    //Save an untouched copy for when sorting/filtering is undone
    private lateinit var itemsListUntouched: MutableList<Items>

    private lateinit var itemsList: MutableList<Items>
    private lateinit var starredCategoryList: MutableList<Categories>
    private var itemsListRecyclerView: RecyclerView? = null
    private var starredCategoriesRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemsAdapter: RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>
    private lateinit var starredCategoryRecyclerViewAdapter : RecyclerView.Adapter<StarredCategoryRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    var additionalData = ""
    private val sortOptions = SortOptions()
    private var filterOptions = ItemsFilterOptions()

    private lateinit var itemTotal: TextView
    private lateinit var result: Pair<MutableList<Items>, Double>

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)

        val searchBar: SearchView = findViewById(R.id.search_bar_text)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)
        itemTotal = findViewById(R.id.items_cost_value)

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
        itemsList = result.first
        //
        var category1 = Categories(1,"mycategory1")
        var category2 = Categories(2,"mycategory2")
        val mutableList : MutableList<Categories> = arrayListOf(category1,category2)
        starredCategoryList=mutableList
        itemTotal.text = result.second.toString()
        itemsListUntouched = itemsList.map { it.copy() }.toMutableList()

        val context = this

        itemsListRecyclerView = findViewById(R.id.items_list)
        starredCategoriesRecyclerView = findViewById(R.id.category_sort)

        progressBar.visibility = View.VISIBLE

        if (itemsList.isEmpty()) {
            itemsListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        //set item list recycler view
        if (itemsListRecyclerView != null) {
            itemsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            itemsListRecyclerView!!.layoutManager = linearLayoutManager
            itemsAdapter = ItemsRecyclerViewAdapter(itemsList, this)

            itemsListRecyclerView!!.adapter = itemsAdapter
            progressBar.visibility = View.GONE
            itemsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //Before loading, revert to the old order
                        itemsList.clear()
                        itemsList.addAll(itemsListUntouched)

                        //Load in more
                        result = loadItemsFromAPI(context, pageSize, additionalData)
                        itemsList = result.first
                        itemTotal.text = result.second.toString()

                        // update the untouched
                        itemsListUntouched = itemsList.map { it.copy() }.toMutableList()

                        //Apply whatever sort is set
                        applyItemSortOptions()

                        //Update the adapter items
                        itemsAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE
                }
            }
            )
        }

        //set category
        if (starredCategoriesRecyclerView != null) {
            starredCategoriesRecyclerView!!.setHasFixedSize(true)
//            linearLayoutManager = LinearLayoutManager(this)
            starredCategoriesRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
            starredCategoryRecyclerViewAdapter = StarredCategoryRecyclerViewAdapter(starredCategoryList)

            starredCategoriesRecyclerView!!.adapter = starredCategoryRecyclerViewAdapter
//            progressBar.visibility = View.GONE
            starredCategoriesRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
//                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        //Before loading, revert to the old order
//                        starredCategoryList.clear()
//                        s.addAll(itemsListUntouched)

                        //Load in more


//                        result = loadItemsFromAPI(context, pageSize, additionalData)
//                        itemsList = result.first
//                        itemTotal.text = result.second.toString()


                        // update the untouched
//                        itemsListUntouched = itemsList.map { it.copy() }.toMutableList()

                        //Apply whatever sort is set
                        applyItemSortOptions()

                        //Update the adapter items
                        starredCategoryRecyclerViewAdapter.notifyDataSetChanged()
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
        itemsAdapter.notifyDataSetChanged()
    }

    /**
     * Sorts the itemList MutableList<Items> based on sortOptions from user.
     */
    private fun applyItemSortOptions() {
        // Restore itemList to the untouched state

        itemsList.clear()
        itemsList.addAll(itemsListUntouched)

        //Sort
        if (sortOptions.isPriceAscending) {
            itemsList.sortBy { it.price }
        }
        if (sortOptions.isPriceDescending) {
            itemsList.sortByDescending { it.price }
        }
        if (sortOptions.isNameAscending) {
            itemsList.sortBy { it.name.lowercase() }
        }
        if (sortOptions.isNameDescending) {
            itemsList.sortByDescending { it.name.lowercase() }
        }
    }

    /**
     * A listener that gets back the filters set in the ItemFilterDialog.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onReturnedFilterOptions(newFilterOptions: ItemsFilterOptions) {
        this.filterOptions = newFilterOptions
        val filterOptionList = ArrayList<String>()
        val sb = StringBuilder("?")
        additionalData = ""

        if (filterOptions.categoryName.isNotEmpty() && filterOptions.categoryId > -1) {
            filterOptionList.add("category_id=${filterOptions.categoryId}")
        }
        //TODO backend doesn't support this filter yet
//        if (filterOptions.categoryName.isNotEmpty() && filterOptions.merchantId > -1) {
//            filterOptionList.add("merchant_id=${filterOptions.merchantId}")
//        }
        if (filterOptions.startDate.isNotEmpty() && filterOptions.endDate.isNotEmpty()) {
            filterOptionList.add(
                "start_date=${filterOptions.startDate}&end_date=${filterOptions.endDate}"
            )
        }
        if (filterOptions.minPrice > 0 && filterOptions.maxPrice > 0) {
            filterOptionList.add("min_price=${filterOptions.minPrice}&max_price=${filterOptions.maxPrice}")
        }

        for (i in 0 until filterOptionList.size) {
            if (i == 0) {
                sb.append(filterOptionList[i])
            } else {
                sb.append("&${filterOptionList[i]}")
            }
        }

        additionalData = sb.toString()

        itemsList.clear()

        pageSize = 1
        pageNumber = 1

        //reload
        result = loadItemsFromAPI(this, pageSize, additionalData)
        itemsList = result.first
        itemTotal.text = result.second.toString()

        // update the untouched
        itemsListUntouched = itemsList.map { it.copy() }.toMutableList()

        //Apply whatever sort is set
        applyItemSortOptions()

        //Update the adapter items
        itemsAdapter.notifyDataSetChanged()
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
            if (posToRemove != null && price != null) {
                val newTotal = (itemTotal.text.toString().toDouble() - price)
                // retrieve the deleted item after deleting
                val removedItem = result.first.removeAt(posToRemove)
                itemsAdapter.notifyItemRemoved(posToRemove)

                //remove the item from untouched
                itemsListUntouched.remove(removedItem)

                //why must pair be val
                result = Pair(result.first, newTotal)
                itemsList = result.first
                itemTotal.text = result.second.toString()
                itemsAdapter.notifyDataSetChanged()
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

    companion object {
        const val ITEM_INFO_ACTIVITY = 6463646
    }
}