package com.codenode.budgetlens.manualReceipt

import android.app.DatePickerDialog
import android.content.Context
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
        val merchant = findViewById<Spinner>(R.id.mercant)
        val sps: SharedPreferences =this.getSharedPreferences("data",Context.MODE_PRIVATE)
        val merchantList:ArrayList<String> =
            sps.getString("merchant","IGA,Costco,Walmart,Subway,ADD")?.split(",") as ArrayList<String>
        var adapter = ArrayAdapter(
            baseContext,
            android.R.layout.simple_spinner_item,
            merchantList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        merchant.adapter = adapter


        val date = findViewById<TextView>(R.id.date)
        date.setOnClickListener{
            initCalendar(this,date)
        }

        val warranties = findViewById<TextView>(R.id.warranties)
        warranties.setOnClickListener{
            initCalendar(this,warranties)
        }
        val returnDate = findViewById<TextView>(R.id.returnDate)
        returnDate.setOnClickListener{
            initCalendar(this,returnDate)
        }
        val timeOfSale = findViewById<TextView>(R.id.timeOfSale)
        timeOfSale.setOnClickListener{
            initCalendar(this,timeOfSale)
        }


        val ctx = this;

        val total = findViewById<TextView>(R.id.total)
        val location = findViewById<TextView>(R.id.location)

        var currency = findViewById<Spinner>(R.id.currency)
        val item = findViewById<TextView>(R.id.item)

        val filledButton = findViewById<Button>(R.id.filledButton)
        filledButton.setOnClickListener {
            val itemTmp = item.text.toString();
            var flag = 0;
            Log.d("1111111",date.text.toString());
            if(date.text.toString()==null|| ("" == date.text.toString())){
                flag++;
                Toast.makeText(this,"please choose a date",Toast.LENGTH_SHORT).show();
            }
            if(total.text.toString()==null|| ("" == total.text.toString())){
                flag++;
                Toast.makeText(this,"please enter a total amount",Toast.LENGTH_SHORT).show();
            }
            if(item.text.toString()==null|| ("" == item.text.toString())){
                flag++;
                Toast.makeText(this,"please enter an item",Toast.LENGTH_SHORT).show();
            }
            if(location.text.toString()==null|| ("" == location.text.toString())){
                flag++;
                Toast.makeText(this,"please enter a location",Toast.LENGTH_SHORT).show();
            }else{
                if(itemTmp.indexOf(",")==-1){
                    flag++;
                    Toast.makeText(this,"please enter a current item price",Toast.LENGTH_SHORT).show();
                }else{
                    val itemArray = itemTmp.split(",");
                    if (itemArray.size>1){
                        for( i in itemArray){
                            val itemArr  = i.split("-");
                            if(itemArr.size>1){
                                val itemPrice = itemArr[1];
                                if( itemPrice.matches("-?\\d+(\\.\\d+)?".toRegex())){
                                    finish()
                                }else{
                                    flag++;
                                    Toast.makeText(this,"please enter a current price",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                flag++;
                                Toast.makeText(this,"please enter a current price",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        val itemArr  = itemTmp.split("-");
                        if(itemArr.size>1){
                            val itemPrice = itemArr[1];
                            if( itemPrice.matches("-?\\d+(\\.\\d+)?".toRegex())){
                                finish()
                            }else{
                                flag++;
                                Toast.makeText(this,"please enter a current price",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            flag++;
                            Toast.makeText(this,"please enter a current price",Toast.LENGTH_SHORT).show();
                        }
                    }


                }

            }

        }
        merchant.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinnerContext =
                    merchantList[position]
                if ("ADD" == spinnerContext){
                    val inputServer = EditText(ctx)
                    inputServer.filters = arrayOf<InputFilter>(LengthFilter(50))
                    val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
                    builder.setTitle("please enter a merchant").setIcon(android.R.drawable.ic_dialog_info)
                        .setView(inputServer)
                        .setNegativeButton("cancel", null)
                    builder.setPositiveButton(
                        "confirm"
                    ) { dialog, _ ->
                        val sign = inputServer.text.toString()
                        Toast.makeText(this@Receipt, sign, Toast.LENGTH_SHORT)
                            .show()
                        if (sign != null && sign.isNotEmpty()) {
                            merchantList.add(0, sign);

                            adapter = ArrayAdapter(
                                baseContext,
                                android.R.layout.simple_spinner_item,
                                merchantList
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            merchant.adapter = adapter
                            var listString = ""
                            for (s in merchantList) {
                                if (s.isNotEmpty()) {
                                    listString += "$s,"
                                }

                            }
                            val editor: SharedPreferences.Editor = sps.edit()
                            editor.putString("merchant", listString)
                            editor.apply()
                            Log.i("11111", listString)
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                        }
                    }
                    builder.show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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