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
import com.codenode.budgetlens.data.UserReceipts.Companion.userReceipts

class ReceiptsListPageActivity : AppCompatActivity() {
    private lateinit var receiptList: MutableList<Receipts>
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>

    private var pageSize = 2

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        userReceipts.clear()

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        receiptList = loadReceiptsFromAPI(this, pageSize)
        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        progressBar.visibility = View.VISIBLE

        if(receiptList.isEmpty()) {
            receiptsListRecyclerView!!.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        val context = this

        if (receiptsListRecyclerView != null) {
            receiptsListRecyclerView!!.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(this)
            receiptsListRecyclerView!!.layoutManager = linearLayoutManager
            adapter = ReceiptsRecyclerViewAdapter(receiptList)
            receiptsListRecyclerView!!.adapter = adapter
            progressBar.visibility = View.GONE
            receiptsListRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("NotifyDataSetChanged")
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        receiptList = loadReceiptsFromAPI(context, pageSize)
                        adapter.notifyDataSetChanged()
                    }
                    progressBar.visibility = View.VISIBLE
                }
            })
        }
    }
}