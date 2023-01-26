package com.codenode.budgetlens.items

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.ImportantDates
import com.codenode.budgetlens.data.UserImportantDates

class DeleteImportantDatePopUpActivity : AppCompatActivity() {
    lateinit var date: ImportantDates
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_important_date_pop_up)

        val displayMetrics= DisplayMetrics()
        windowManager.getDefaultDisplay().getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.7).toInt(), (height * 0.3).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Create the Intent to go back to the item info activity page
        val goToItemInfoActivity = Intent(this, ItemInfoActivity::class.java)


        // Find the important dates to be removed in this activity
        val extras = intent.getIntExtra("id", 0)
        val itemPosition = intent.getStringExtra("position")

        Log.d("Debug extras", extras.toString())
        for (i in UserImportantDates.userImportantDates) {
            if (i.id.toString() == extras.toString()) {
                date = i
                break
            }
        }
        // Change the text in the popup to reflect the activity
        findViewById<TextView>(R.id.delete_question).text =
            "Delete reminder ${date.date}, ${date.description} from this item?"

        // Cancel button goes back to item info activity
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        cancelButton.setOnClickListener {
            goToItemInfoActivity.putExtra("itemId", date.item.toString())
            goToItemInfoActivity.putExtra("position", itemPosition)
            startActivity(goToItemInfoActivity)
        }

        // Confirm button goes back to item info activity and deletes the important dates
        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            UserImportantDates.deleteImportantDatesFromAPI(this, date)
            goToItemInfoActivity.putExtra("itemId", date.item.toString())
            goToItemInfoActivity.putExtra("position", itemPosition)
            startActivity(goToItemInfoActivity)
        }
    }
}