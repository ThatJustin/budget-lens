package com.codenode.budgetlens.items


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Items


class ItemsRecyclerViewAdapter(
    private val items: MutableList<Items>,
    val itemListActivity: ItemListActivity
) :
    RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text =
            holder.itemView.context.getString(R.string.item_name, item.name)

        holder.itemPrice.text =
            holder.itemView.context.getString(R.string.price, item.price)

        holder.itemDate.text =
            holder.itemView.context.getString(R.string.scan_date, item.scan_dates)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val itemDate: TextView = itemView.findViewById(R.id.months)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = items[position]
                itemListActivity.openItemInfoActivity(item, position)
            }
        }
    }
}