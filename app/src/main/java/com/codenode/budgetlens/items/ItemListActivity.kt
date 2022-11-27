package com.codenode.budgetlens.items

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
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

class ItemListActivity : AppCompatActivity() {
    private lateinit var itemList: MutableList<Items>
    private var itemsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemAdapter: RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>
    private var pageSize = 5
    private lateinit var itemTotal: TextView
    private lateinit var result: Pair<MutableList<Items>, Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        //search/sort/filter variable
        var additionalData = ""
        val searchBar: SearchView = findViewById(R.id.search_bar_text)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.ITEMS, this, this.window.decorView)
        itemTotal = findViewById(R.id.item_cost_value)

        userItems.clear()

        pageNumber = 1

        val progressBar: ProgressBar = findViewById(R.id.progressBar)


        //load the list

        result = loadItemsFromAPI(this, pageSize, additionalData)
        itemList = result.first
        itemTotal.text = result.second.toString()


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
                        result = loadItemsFromAPI(context, pageSize, additionalData)
                        itemList = result.first
                        itemTotal.text = result.second.toString()
                        itemAdapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.GONE
                }
            }
            )
        }
    }

    /**
     * Handles results from opened activities by this activity.
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_INFO_ACTIVITY) {
            val posToRemove = data?.getIntExtra("position", -1)
            val price = data?.getDoubleExtra("price", 0.0)
            if (posToRemove != null && price != null) {
                val newTotal = (itemTotal.text.toString().toDouble() - price)
                result.first.removeAt(posToRemove)
                //why must pair be val
                result = Pair(result.first, newTotal)
                itemTotal.text = result.second.toString()
                itemAdapter.notifyItemRemoved(posToRemove)
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