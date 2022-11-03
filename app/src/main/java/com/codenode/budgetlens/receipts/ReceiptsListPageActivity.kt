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
import com.codenode.budgetlens.data.UserReceipts
import com.codenode.budgetlens.data.UserReceipts.Companion.loadReceiptsFromAPI

class ReceiptsListPageActivity : AppCompatActivity() {
    private lateinit var receiptList: MutableList<Receipts>
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>

    var pageSize = 5

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        receiptList = loadReceiptsFromAPI(this, pageSize)

        val context = this;
        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        if (receiptsListRecyclerView != null) {
            linearLayoutManager = LinearLayoutManager(this)
            receiptsListRecyclerView!!.layoutManager = linearLayoutManager
            adapter = ReceiptsRecyclerViewAdapter(receiptList)
            receiptsListRecyclerView!!.adapter = adapter
            receiptsListRecyclerView!!.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        progressBar.visibility = View.VISIBLE
                        receiptList = loadReceiptsFromAPI(context, pageSize)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
        }
    }
}