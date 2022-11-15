package com.codenode.budgetlens.manualReceipt

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import java.util.*


open class Receipt() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        var mercant = findViewById<Spinner>(R.id.mercant)
        var sps: SharedPreferences =this.getSharedPreferences("data",Context.MODE_PRIVATE)
        var mercantList:ArrayList<String> =
            sps.getString("mercant","IGA,Costco,Walmart,Subway,ADD")?.split(",") as ArrayList<String>
        var adapter = ArrayAdapter(
            baseContext,
            android.R.layout.simple_spinner_item,
            mercantList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mercant.setAdapter(adapter)


        val date = findViewById<TextView>(R.id.date)
        date.setOnClickListener{
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


        var ctx = this;

        val total = findViewById<TextView>(R.id.total)
        val location = findViewById<TextView>(R.id.location)

        var currency = findViewById<Spinner>(R.id.currency)
        val item = findViewById<TextView>(R.id.item)

        val filledButton = findViewById<Button>(R.id.filledButton)
        filledButton.setOnClickListener {
            val itemTmp = item.text.toString();
            Log.d("1111111",date.text.toString());
            if(date.text.toString()==null|| ("".equals(date.text.toString()))){
                Toast.makeText(this,"Please choose date",Toast.LENGTH_SHORT).show();
            }else if(total.text.toString()==null|| ("".equals(total.text.toString()))){
                Toast.makeText(this,"Please enter total",Toast.LENGTH_SHORT).show();
            }else if(item.text.toString()==null|| ("".equals(item.text.toString()))){
                Toast.makeText(this,"Please enter item",Toast.LENGTH_SHORT).show();
            }else if(location.text.toString()==null|| ("".equals(location.text.toString()))){
                Toast.makeText(this,"Please enter location",Toast.LENGTH_SHORT).show();
            }else{
                if(itemTmp.indexOf(",")==-1){
                    Toast.makeText(this,"Please enter current item-price",Toast.LENGTH_SHORT).show();
                }else{
                    val itemArray = itemTmp.split(",");
                    if (itemArray.size>1){
                        for( i in itemArray){
                            val itemArr  = i.split("-");
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
                    }else{
                        val itemArr  = itemTmp.split("-");
                        if(itemArr.size>1){
                            val item_price = itemArr[1];
                            if( item_price.matches("-?\\d+(\\.\\d+)?".toRegex())){
                                finish()
                            }else{
                                Toast.makeText(this,"please enter current price",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this,"please enter current price",Toast.LENGTH_SHORT).show();
                        }
                    }


                }

            }

        }
        mercant.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var spinnercontext =
                    mercantList.get(position)
                if ("ADD".equals(spinnercontext)){
                    val inputServer = EditText(ctx)
                    inputServer.filters = arrayOf<InputFilter>(LengthFilter(50))
                    val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
                    builder.setTitle("pleace enter mercant").setIcon(android.R.drawable.ic_dialog_info)
                        .setView(inputServer)
                        .setNegativeButton("cancel", null)
                    builder.setPositiveButton(
                        "confirm",
                        DialogInterface.OnClickListener { dialog, which ->
                            val _sign = inputServer.text.toString()
                            Toast.makeText(this@Receipt, _sign, Toast.LENGTH_SHORT)
                                .show()
                            if (_sign != null && !_sign.isEmpty()) {
                                mercantList.add(0,_sign);

                                adapter = ArrayAdapter(
                                    baseContext,
                                    android.R.layout.simple_spinner_item,
                                    mercantList
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                mercant.setAdapter(adapter)
                                var listString:String=""
                                for (s in mercantList) {
                                    if(s.length!=0){
                                        listString += s + ","
                                    }

                                }
                                var editor:SharedPreferences.Editor=sps.edit()
                                editor.putString("mercant",listString)
                                editor.commit()
                                Log.i("11111",listString.toString())
                                dialog.dismiss();

                            } else {
                                dialog.dismiss();
                            }
                        })
                    builder?.show()
                }
//                Log.i("11111:",position.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
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