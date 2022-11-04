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

    private var pageSize = 3

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        //API call which helps retrieve receipts from server
        receiptList = loadReceiptsFromAPI(this, pageSize)

//        val testingReceipts = Receipts(
//            123,
//            "Castco",
//            "2022/02/02 - 13:54",
//            "https://www.researchgate.net/publication/321637231/figure/fig1/AS:596617438699520@1519256090021/Example-of-actual-receipt.png",
//            "--",
//            100.25,
//            2.01,
//            0.0,
//            0,
//            "CAD",
//            "2022/02/16"
//        )
//        receiptList.add(testingReceipts)

        val context = this;
        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

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
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        progressBar.visibility = View.VISIBLE
                        receiptList = loadReceiptsFromAPI(context, pageSize)
                        adapter.notifyDataSetChanged()
                    }
                }
            })
        }
    }
}