package com.codenode.budgetlens.category


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Category
import com.codenode.budgetlens.data.UserCategories


class CategoryRecyclerViewAdapter(private val categories: MutableList<Category>) :
    RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryRecyclerViewAdapter.ViewHolder, position: Int) {
        val imageStar: ImageView = holder.itemView.findViewById(R.id.image_star)
        val imageGarbage: ImageView = holder.itemView.findViewById(R.id.image_garbage)
        val category = categories[position]
        holder.categoryName.text =
            holder.itemView.context.getString(R.string.category_name, category.category_name)
        if (category.category_toggle_star) {
            imageStar.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            imageStar.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }

        if (category.parent_category_id != null) {
            imageGarbage.setImageResource(R.drawable.ic_baseline_delete_outline_24)
        }
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
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        var imageStar: ImageView = itemView.findViewById(R.id.image_star)
        val imageGarbage: ImageView = itemView.findViewById(R.id.image_garbage)

        init {
            categoryName.setOnClickListener(this)
            imageStar.setOnClickListener(this)
            imageGarbage.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val category = categories[position]
                when (v?.id) {
                    imageStar.id -> {
                        UserCategories.toggleStarFromAPI(context, category, imageStar)
                        println("Clicked Star on category $category")
                    }
                    imageGarbage.id -> {
                        UserCategories.deleteSubCategoryFromAPI(context, category, this)
                        println("Clicked Delete on category $category")
                    }
                    else -> {
                        println("Clicked $category")
                    }
                }

            }
        }
    }

}