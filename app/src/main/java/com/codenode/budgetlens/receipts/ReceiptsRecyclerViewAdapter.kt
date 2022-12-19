package com.codenode.budgetlens.receipts

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Receipts
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class ReceiptsRecyclerViewAdapter(private val receipts: MutableList<Receipts>) :
    RecyclerView.Adapter<ReceiptsRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReceiptsRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.receipts_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptsRecyclerViewAdapter.ViewHolder, position: Int) {
        val receipt = receipts[position]
        if (receipt.merchant_name == null) {
            holder.merchantName.text = holder.itemView.context.getString(R.string.merchant_name, "N/A")
        }
        else {
            holder.merchantName.text = holder.itemView.context.getString(R.string.merchant_name, receipt.merchant_name)
        }
        if (receipt.scan_date == null) {
            val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            holder.scanDate.text = holder.itemView.context.getString(R.string.scan_date, date)
        } else {
            holder.scanDate.text = holder.itemView.context.getString(R.string.scan_date, receipt.scan_date)
        }
        if (receipt.total_amount == null) {
            holder.totalAmount.text = holder.itemView.context.getString(R.string.total_amount, 0.00)
        } else {
            holder.totalAmount.text = holder.itemView.context.getString(R.string.total_amount, receipt.total_amount)
        }
        if (receipt.receipt_image == null) {
            holder.receiptImage.scaleType = ImageView.ScaleType.CENTER
            Glide.with(holder.itemView.context).load(receipt.receipt_image).placeholder(R.drawable.ic_baseline_receipt_long_24).into(holder.receiptImage)
        } else {
            holder.receiptImage.scaleType = ImageView.ScaleType.CENTER
            Glide.with(holder.itemView.context).load(receipt.receipt_image).into(holder.receiptImage)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val merchantName: TextView = itemView.findViewById(R.id.merchant_name)
        val scanDate: TextView = itemView.findViewById(R.id.scan_date)
        val totalAmount: TextView = itemView.findViewById(R.id.total_amount)
        val receiptImage: ImageView = itemView.findViewById(R.id.receipt_image_thumbnail_preview)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition

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
        val activity = context as Activity
        Snackbar.make(
            activity.findViewById<BottomNavigationView>(R.id.bottom_navigation),
            "Receipt deleted.",
            Snackbar.LENGTH_SHORT
        ).show()
        receipts.removeAt(position)
        notifyItemRemoved(position)
    }
}