package com.codenode.budgetlens.receipts

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Receipts
import com.google.android.material.snackbar.Snackbar

class ReceiptsRecyclerViewAdapter (private val receipts: MutableList<Receipts>) : RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptsRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.receipts_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptsRecyclerViewAdapter.ViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.merchantName.text = holder.itemView.context.getString(R.string.merchant_name, receipt.merchant_name)
        holder.scanDate.text = holder.itemView.context.getString(R.string.scan_date, receipt.scan_date)
        holder.totalAmount.text = holder.itemView.context.getString(R.string.total_amount, receipt.total_amount)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val merchantName: TextView = itemView.findViewById(R.id.merchant_name)
        val scanDate: TextView = itemView.findViewById(R.id.scan_date)
        val totalAmount: TextView = itemView.findViewById(R.id.total_amount)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                val receipt = receipts[position]
                val dialog = ReceiptInfoDialog(context!!, receipt)
                dialog.setOnDismissListener {
                    if (dialog.isDeletedReceipt) {
                        removeReceipt(position)
                    }
                }
                    dialog.show()
            }
        }
    }

    private fun removeReceipt(position: Int) {
//        Snackbar.make(
//            (Activity)context.findViewById<BottomNavigationView>(R.id.bottom_navigation),
//                            "Receipt deleted.",
//                            Snackbar.LENGTH_SHORT
//                        ).show()

        receipts.removeAt(position)
        notifyItemRemoved(position)

    }
}