package com.codenode.budgetlens.receipts

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Receipts

class ReceiptsRecyclerViewAdapter (private val receipts: List<Receipts>) : RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptsRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.receipts_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptsRecyclerViewAdapter.ViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.scanDate.text = Resources.getSystem().getString(R.string.scan_date, receipt.scan_date)
        holder.receiptImageURL.text = Resources.getSystem().getString(R.string.receipt_image, receipt.receipt_image)
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val scanDate: TextView = itemView.findViewById(R.id.scan_date)
        val receiptImageURL: TextView = itemView.findViewById(R.id.receipt_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val receipt = receipts[position]
            }
        }
    }
}