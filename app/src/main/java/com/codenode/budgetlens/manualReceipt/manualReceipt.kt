package com.codenode.budgetlens.manualReceipt

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import java.util.*


open class Receipt() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        val date = findViewById<TextView>(R.id.date)
        date.setOnClickListener{
//            date.setInputType(InputType.TYPE_NULL);
            initCalendar(this,date)
        }

        /*var mercant = findViewById<Spinner>(R.id.mercant)
        mercant.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                var spinnercontext =
//                    this@Receipt.getResources().getStringArray(R.array.option).get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })*/
    }

    private fun initCalendar(context: Context, textView: TextView) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            context,
            { view, year, month, dayOfMonth ->
                val text = year.toString() + "/" + (month + 1) + "/" + dayOfMonth
                textView.text = text
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        dialog.show()
    }
}