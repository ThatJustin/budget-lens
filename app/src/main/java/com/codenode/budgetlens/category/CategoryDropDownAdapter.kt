package com.codenode.budgetlens.category

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Category

// Create an adapter class that extends the ArrayAdapter class. This adapter will be responsible for populating the spinner with the list of objects.
// In the adapter's constructor, pass in the current context, the spinner item layout, and the list of objects.
class CategoryDropdownAdapter(var context: Context, private val categories: MutableList<Category>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.category_dropdown_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.bind(categories[position], context)
        view.setBackgroundColor(Color.WHITE)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.category_dropdown_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.bind(categories[position], context)

        return view
    }

    override fun getItem(position: Int): Any = categories[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = categories.size

    private class ViewHolder(view: View) {
        private val categoryName: TextView = view.findViewById(R.id.textView)
        private val categoryIcon: ImageView = view.findViewById(R.id.imageView)

        fun bind(category: Category, context: Context) {

            categoryName.text = category.category_name
            if (category.icon != "") {
                val id = context.resources.getIdentifier("@drawable/" + category.icon, null, context.packageName)
                categoryIcon.setImageResource(id)
            }
            else{
                categoryIcon.setImageResource(R.drawable.ic_baseline_category_24)
            }

        }
    }
}
