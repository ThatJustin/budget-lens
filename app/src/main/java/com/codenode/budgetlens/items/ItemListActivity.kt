package com.codenode.budgetlens.items

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Items
import com.codenode.budgetlens.data.UserItems.Companion.loadItemsFromAPI
import com.codenode.budgetlens.data.UserItems.Companion.pageNumber
import com.codenode.budgetlens.data.UserItems.Companion.userItems

class ItemListActivity : AppCompatActivity() {
    private lateinit var itemList: MutableList<Items>
    private var itemsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemAdapter: RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        //search/sort/filter variable
        var additionalData = ""
        val searchBar: SearchView = findViewById(R.id.search_bar_text)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)

        userItems.clear()
        pageNumber = 1

        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        //load the list

        itemList = loadItemsFromAPI(this, pageSize, additionalData)
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
            itemAdapter = ItemsRecyclerViewAdapter(itemList)

            itemsListRecyclerView!!.adapter = itemAdapter
            progressBar.visibility = View.GONE
            itemsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    progressBar.visibility = View.VISIBLE
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        itemList = loadItemsFromAPI(context, pageSize, additionalData)
                        itemAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE
                }
            }
            )
        }
    }
}