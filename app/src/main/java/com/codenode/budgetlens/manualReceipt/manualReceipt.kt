package com.codenode.budgetlens.manualReceipt

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import java.util.*

open class Receipt() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        val img1 = getResources().getDrawable(R.drawable.calendar);
        img1.setBounds(0,0,80,80);
        val date = findViewById<TextView>(R.id.date)
        date.setCompoundDrawables(null, null, img1, null);
        date.setOnClickListener{
//            date.setInputType(InputType.TYPE_NULL);
            initCalendar(this,date)
        }

        val warranties = findViewById<TextView>(R.id.warranties)
        warranties.setOnClickListener{
            initCalendar(this,warranties)
        }
        var returnDate = findViewById<TextView>(R.id.returnDate)
        returnDate.setOnClickListener{
            initCalendar(this,returnDate)
        }
        val timeOfSale = findViewById<TextView>(R.id.timeOfSale)
        timeOfSale.setOnClickListener{
            initCalendar(this,timeOfSale)
        }



        val img2 = getResources().getDrawable(R.drawable.currency);
        var img3 = getResources().getDrawable(R.drawable.location);
        var img4 = getResources().getDrawable(R.drawable.calendar);
        var img5 = getResources().getDrawable(R.drawable.home);
        val total = findViewById<TextView>(R.id.total)
        val location = findViewById<TextView>(R.id.location)
        var mercant = findViewById<Spinner>(R.id.mercant)
        var currency = findViewById<Spinner>(R.id.currency)
        val item = findViewById<TextView>(R.id.item)
        img2.setBounds(0,0,80,80);
        img3.setBounds(0,0,80,80);
        img4.setBounds(0,0,80,80);
        total.setCompoundDrawables(null, null, img2, null);
        location.setCompoundDrawables(null, null, img3, null);
        warranties.setCompoundDrawables(null, null, img4, null);
        returnDate.setCompoundDrawables(null, null, img4, null);
        timeOfSale.setCompoundDrawables(null, null, img4, null);

        val filledButton = findViewById<Button>(R.id.filledButton)
        filledButton.setOnClickListener {
            val itemTmp = item.text.toString();
            Log.d("1111111",date.text.toString());
            if(date.text.toString()==null|| ("".equals(date.text.toString()))){
                Toast.makeText(this,"pleace choose date",Toast.LENGTH_SHORT).show();
            }else if(total.text.toString()==null|| ("".equals(total.text.toString()))){
                Toast.makeText(this,"pleace enter total",Toast.LENGTH_SHORT).show();
            }else if(item.text.toString()==null|| ("".equals(item.text.toString()))){
                Toast.makeText(this,"pleace enter item",Toast.LENGTH_SHORT).show();
            }else if(location.text.toString()==null|| ("".equals(location.text.toString()))){
                Toast.makeText(this,"pleace enter location",Toast.LENGTH_SHORT).show();
            }else{
                if(itemTmp.indexOf("-")==-1){
                    Toast.makeText(this,"pleace enter current item-price",Toast.LENGTH_SHORT).show();
                }else{
                    val itemArr  = itemTmp.split("-");
                    if(itemArr.size>1){
                        val item_price = itemArr[1];
                        if( item_price.matches("-?\\d+(\\.\\d+)?".toRegex())){
                            finish()
                        }else{
                            Toast.makeText(this,"pleace enter current price",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this,"pleace enter current price",Toast.LENGTH_SHORT).show();
                    }
                }

            }

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
