package com.codenode.budgetlens.receipts

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Receipts
import com.codenode.budgetlens.data.UserReceipts.Companion.loadReceiptsFromAPI

class ReceiptsListPageActivity : AppCompatActivity() {
    private lateinit var receiptList: MutableList<Receipts>
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>
    private var isLoading = false

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        receiptList = loadReceiptsFromAPI(this)

        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        if (receiptsListRecyclerView != null) {
            linearLayoutManager = LinearLayoutManager(this)
            receiptsListRecyclerView!!.layoutManager = linearLayoutManager
            adapter = ReceiptsRecyclerViewAdapter(receiptList)
            receiptsListRecyclerView!!.adapter = adapter
            receiptsListRecyclerView!!.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        isLoading = true
                        progressBar.visibility = View.VISIBLE
                        val visibleItemCount = linearLayoutManager.childCount
                        val totalItemCount = linearLayoutManager.itemCount
                        val pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                        if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                    isLoading = false
                    progressBar.visibility = View.GONE
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                        println("Reached end of list")
                        adapter.notifyDataSetChanged()
                    }
                }
            })
        }
    }
}