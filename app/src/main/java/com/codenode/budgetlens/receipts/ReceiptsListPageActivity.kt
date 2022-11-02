package com.codenode.budgetlens.receipts

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Receipts
import com.codenode.budgetlens.data.UserReceipts.Companion.loadReceiptsFromAPI

class ReceiptsListPageActivity : AppCompatActivity() {
    private var receiptList: List<Receipts> = listOf(Receipts())
    private var receiptsListRecyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.RECEIPTS, this, this.window.decorView)

        receiptsListRecyclerView = findViewById(R.id.receipts_list)
        linearLayoutManager = LinearLayoutManager(this)
        receiptsListRecyclerView?.layoutManager = linearLayoutManager
        adapter = ReceiptsRecyclerViewAdapter(loadReceiptsFromAPI(this))
        receiptsListRecyclerView?.adapter = adapter
    }
}