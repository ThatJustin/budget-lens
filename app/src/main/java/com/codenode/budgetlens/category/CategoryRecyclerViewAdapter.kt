package com.codenode.budgetlens.category


import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat.startActivity
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
            // Add garbage bin to subcategories
            imageGarbage.setImageResource(R.drawable.ic_baseline_delete_outline_24)

            holder.itemView.findViewById<LinearLayout>(R.id.category_card).setBackgroundColor(Color(0xf7, 0xf2, 0xf9).toArgb())
//            holder.itemView.setBackgroundColor(Color(0xf7, 0xf2, 0xf9).toArgb())
            holder.itemView.findViewById<ImageView>(R.id.image_category).setImageResource(0)
            holder.itemView.elevation = 0.0F

            // Go to popup delete page for deleting the sub category
            val gotToDeleteSubCategoryPopUp =
                Intent(holder.itemView.context, DeleteSubCategoryPopUpActivity::class.java)
            // Add the category name as an extra intent value to send to the delete popup page.
            gotToDeleteSubCategoryPopUp.putExtra("category", category.category_name)
            imageGarbage.setOnClickListener {
                holder.itemView.context.startActivity(gotToDeleteSubCategoryPopUp)
            }
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
                        println("Deleted SubCategory $category")
                    }
                    else -> {
                        println("Clicked $category")
                    }
                }

            }
        }
    }

}