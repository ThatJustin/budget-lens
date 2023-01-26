package com.codenode.budgetlens.items


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.ImportantDates


class ImportantDatesRecyclerViewAdapter(
    private val important_dates: MutableList<ImportantDates>
) : RecyclerView.Adapter<ImportantDatesRecyclerViewAdapter.ViewHolder>() {
    var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ImportantDatesRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_important_dates_model, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ImportantDatesRecyclerViewAdapter.ViewHolder, position: Int
    ) {
        val importantDate = important_dates[position]
        holder.importantDateString.text = holder.itemView.context.getString(
            R.string.important_date_date, importantDate.date
        )

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return important_dates.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val importantDateString: TextView = itemView.findViewById(R.id.important_date_date)
        private val importantDatesGarbage:ImageView = itemView.findViewById(R.id.remove_important_dates)
        init {
            itemView.setOnClickListener(this)
            importantDatesGarbage.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val importantDate = important_dates[position]
                when(v?.id){
                    importantDatesGarbage.id ->{
                        println("Deleted Important dates on date $importantDate")
                        val goToDeleteImportantDatesPopUp = Intent(context,DeleteImportantDatePopUpActivity::class.java)
                        goToDeleteImportantDatesPopUp.putExtra("id", importantDate.id)

                        context?.startActivity(goToDeleteImportantDatesPopUp)
                    }
                }
            }
        }


    }
}