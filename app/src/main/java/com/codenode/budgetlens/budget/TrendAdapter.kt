package com.codenode.budgetlens.budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Trend


class TrendAdapter(private val dataSet: List<Trend>, val context: Context) :
    RecyclerView.Adapter<TrendAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTv: TextView
        val iconIv: ImageView
        init {
            titleTv = view.findViewById(R.id.title)
            iconIv = view.findViewById(R.id.icon)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.trend_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.titleTv.text = dataSet[position].name
        viewHolder.iconIv.setImageResource(dataSet[position].icon)
    }

    override fun getItemCount() = dataSet.size
}
