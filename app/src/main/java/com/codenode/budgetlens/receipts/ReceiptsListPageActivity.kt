package com.codenode.budgetlens.receipts

import android.annotation.SuppressLint
import android.view.View
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

class ReceiptsListPageActivity : AppCompatActivity() {
    private lateinit var receiptList: MutableList<Receipts>
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>

    private var pageSize = 5

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)
        var additionalData = ""
        val searchBar: SearchView = findViewById(R.id.search_bar_text)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        userReceipts.clear()
        pageNumber = 1

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        receiptList = loadReceiptsFromAPI(this, pageSize, additionalData)

        val context = this
        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        progressBar.visibility = View.VISIBLE

        if (receiptList.isEmpty()) {
            receiptsListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        if (receiptsListRecyclerView != null) {
            receiptsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            receiptsListRecyclerView!!.layoutManager = linearLayoutManager
            adapter = ReceiptsRecyclerViewAdapter(receiptList)
            receiptsListRecyclerView!!.adapter = adapter
            progressBar.visibility = View.GONE
            receiptsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        receiptList = loadReceiptsFromAPI(context, pageSize, additionalData)
                        adapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.VISIBLE
                }
            })
            //listener for search bar input
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    additionalData += "?search=" + searchBar.query
                    userReceipts.clear()
                    receiptList = loadReceiptsFromAPI(context, pageSize, additionalData)
                    adapter.notifyDataSetChanged()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }
    }
}