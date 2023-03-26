package com.codenode.budgetlens.items


import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Items
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class ItemsRecyclerViewAdapter(
    val itemListActivity: ItemsListPageActivity
) : RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    private var items: MutableList<Items> = mutableListOf()
    private var unsortedItems: MutableList<Items> = mutableListOf()
    private var totalPrice: Double = 0.00

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemsRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_list_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = holder.itemView.context.getString(R.string.item_name, item.name)

        holder.itemPrice.text = holder.itemView.context.getString(R.string.price, item.price)

        holder.itemDate.text =
            holder.itemView.context.getString(R.string.scan_date, item.scan_dates)

        holder.itemCategory.text =
            holder.itemView.context.getString(R.string.category_name, item.category_name)
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
        val itemCategory: TextView = itemView.findViewById(R.id.item_category)

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

    /**
     * Removes an item at the selected position and sends a snackbar message to indicate that to the user.
     */
    fun removeItem(position: Int, newPrice: Double) {
        val activity = context as Activity
        Snackbar.make(
            activity.findViewById<BottomNavigationView>(R.id.bottom_navigation),
            "Item deleted.",
            Snackbar.LENGTH_SHORT
        ).show()
        items.removeAt(position)
        notifyItemRemoved(position)
        checkShowRecyclerView()
        updateTotalPrice(newPrice)
    }

    /**
     * Checks if the recycler view has items and shows them otherwise it shows
     * a message indicating no found items.
     */
    private fun checkShowRecyclerView() {
        val activity = context as Activity

        val itemListRecyclerView = activity.findViewById<RecyclerView>(R.id.items_list)
        val emptyViewMsg = activity.findViewById<TextView>(R.id.empty_view_msg)

        if (items.isEmpty()) {
            itemListRecyclerView!!.visibility = View.GONE
            emptyViewMsg.visibility = View.VISIBLE
        } else {
            itemListRecyclerView!!.visibility = View.VISIBLE
            emptyViewMsg.visibility = View.GONE
        }
    }

    /**
     * Returns a deep copy mutable list of unsorted items.
     */
    fun getUnsortedItems(): MutableList<Items> {
        return unsortedItems.toMutableList()
    }

    /**
     * Reverts the currently applied sorting options from the items.
     */
    fun revertAppliedSort() {
        this.items.clear()
        this.items.addAll(getUnsortedItems())
    }

    /**
     * Updates the currently viewable items with the sorted version.
     */
    fun sortItems(sortedItemsList: MutableList<Items>) {
        this.items.clear()
        this.items.addAll(sortedItemsList)
        notifyDataSetChanged()
        checkShowRecyclerView()
    }

    /**
     * Adds new items to the viewable recyclerview.
     */
    fun addItems(
        itemsList: MutableList<Items>,
        applyItemsSortOptions: (MutableList<Items>) -> MutableList<Items>,
        newPrice: Double
    ) {
        unsortedItems.addAll(itemsList)
        revertAppliedSort()
        val sortedItems: MutableList<Items> = applyItemsSortOptions(getUnsortedItems())
        items.clear()
        items.addAll(sortedItems)
        notifyDataSetChanged()
        checkShowRecyclerView()
        updateTotalPrice(newPrice)
    }

    /**
     * Modifies the recycler view to show the searched items.
     */
    fun addSearchedItems(
        itemsList: MutableList<Items>,
        applyItemsSortOptions: (MutableList<Items>) -> MutableList<Items>,
        newPrice: Double
    ) {
        // New dataset, reset totalPrice to 0
        totalPrice = 0.00
        unsortedItems.clear()
        unsortedItems.addAll(itemsList)
        val sortedItems: MutableList<Items> = applyItemsSortOptions(itemsList)
        items.clear()
        items.addAll(sortedItems)
        notifyDataSetChanged()
        checkShowRecyclerView()
        updateTotalPrice(newPrice)
    }

    /**
     * Modifies the recycler view to show the filtered items.
     */
    fun addFilteredItems(
        itemsList: MutableList<Items>,
        applyItemsSortOptions: (MutableList<Items>) -> MutableList<Items>,
        newPrice: Double
    ) {
        // New dataset, reset totalPrice to 0
        totalPrice = 0.00
        unsortedItems.clear()
        unsortedItems.addAll(itemsList)
        val sortedItems: MutableList<Items> = applyItemsSortOptions(itemsList)
        items.clear()
        items.addAll(sortedItems)
        notifyDataSetChanged()
        checkShowRecyclerView()
        updateTotalPrice(newPrice)
    }

    /**
     * Updates the total price shown on the item list activity page.
     */
    private fun updateTotalPrice(newPrice: Double) {
        val activity = context as Activity
        val itemTotalView = activity.findViewById<TextView>(R.id.items_cost_value)
        totalPrice += newPrice
        val newItemTotal = "$${
            activity.getString(
                R.string.items_total, totalPrice
            )
        }"
        itemTotalView.text = newItemTotal
    }
}