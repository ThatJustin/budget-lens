package com.codenode.budgetlens.common

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.codenode.budgetlens.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class CommonComponents {
    companion object {

        fun handleNavigationBar(view: View) {
            val myBottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

            myBottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        // Goes nowhere, we're already there
                        true
                    }
                    R.id.receipt -> {
                        // Respond to navigation item 2 click
//                    val intent = Intent(this, ReceiptActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                        true
                    }
                    R.id.budget -> {
                        // Respond to navigation item 2 click
//                    val intent = Intent(this, BudgetActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                        true
                    }
                    R.id.friends -> {
                        // Respond to navigation item 2 click
//                    val intent = Intent(this, FriendsActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                        true
                    }
                    R.id.calendar -> {
                        // Respond to navigation item 2 click
//                    val intent = Intent(this, CalendarActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                        true
                    }

                    else -> false
                }
            }
        }

        fun handleTopAppBar(view: View, context: Context, layoutInflater: LayoutInflater) {
            val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar);
            topAppBar.setNavigationOnClickListener {
                // Handle navigation icon press
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_icon -> {
                        //Do nothing
                        true
                    }
                    R.id.sub_profile -> {
                        //Do nothing for now

                        true
                    }
                    R.id.sub_edit_profile -> {
                        val builder = AlertDialog.Builder(context, R.style.fullscreendialog)
                        val dialogView: View =
                            layoutInflater.inflate(R.layout.edit_profile_dialog, null)
                        builder.setView(dialogView)
                        var dialog = builder.create()
                        dialog.show()

                        val dateOfBirth =
                            dialogView.findViewById<View>(R.id.dateOfBirth) as TextView
                        dateOfBirth.setOnClickListener { initCalendar(context, dateOfBirth) }

                        true
                    }
                    else -> false
                }
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

}