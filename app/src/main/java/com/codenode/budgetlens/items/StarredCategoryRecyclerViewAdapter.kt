package com.codenode.budgetlens.items

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Categories

class StarredCategoryRecyclerViewAdapter(
    private val categories: MutableList<Categories>
) : RecyclerView.Adapter<StarredCategoryRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StarredCategoryRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.starred_category_sort_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StarredCategoryRecyclerViewAdapter.ViewHolder,
        position: Int
    ) {
        val category = categories[position]
        holder.categoryName.text =
            holder.itemView.context.getString(R.string.starred_categories, category.category_name)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val categoryName: TextView = itemView.findViewById(R.id.category_chip)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val category = categories[position]

            }
        }
    }
}