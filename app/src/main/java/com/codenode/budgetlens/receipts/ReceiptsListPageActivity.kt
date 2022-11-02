package com.codenode.budgetlens.receipts

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Receipts
import com.codenode.budgetlens.data.UserReceipts.Companion.loadReceiptsFromAPI

class ReceiptsListPageActivity : AppCompatActivity() {
    private var receiptList: List<Receipts> = listOf()
    private var receiptsList: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipts_list_page)

        loadReceiptsFromAPI(this)

        receiptsList.apply {
            linearLayoutManager = LinearLayoutManager(this@ReceiptsListPageActivity)
            adapter = ReceiptsRecyclerViewAdapter(receiptList)
        }
    }

    override fun onStart() {
        super.onStart()
        finish()
    }
}