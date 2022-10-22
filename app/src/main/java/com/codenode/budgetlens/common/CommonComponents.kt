package com.codenode.budgetlens.common

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.UserProfile
import com.codenode.budgetlens.home.HomePageActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class CommonComponents {
    companion object {

        /**
         * Handles navigation bar on every page page that has it.
         * Each activity must have a BottomNavigationView with the "bottom_navigation" as its id or it will crash.
         */
        fun handleNavigationBar(currentActivityName: ActivityName, context: Context, view: View) {
            val activity: Activity = context as Activity

            val myBottomNavigationView =
                view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

            myBottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        if (currentActivityName != ActivityName.HOME) {
                            val intent = Intent(context, HomePageActivity::class.java)
                            context.startActivity(intent)
                            activity.overridePendingTransition(0, 0)
                        }
                        true
                    }
                    R.id.receipt -> {
                        if (currentActivityName != ActivityName.RECEIPT) {
//                            val intent = Intent(context, ReceiptActivity::class.java)
//                            context.startActivity(intent)
//                            activity.overridePendingTransition(0, 0)
                        }
                        true
                    }
                    R.id.budget -> {
                        if (currentActivityName != ActivityName.BUDGET) {
                            //Might open a menu?
                        }
                        true
                    }
                    R.id.friends -> {
                        if (currentActivityName != ActivityName.FRIENDS) {
//                            val intent = Intent(context, FriendsActivity::class.java)
//                            context.startActivity(intent)
//                            activity.overridePendingTransition(0, 0)
                        }
                        true
                    }
                    R.id.calendar -> {
                        if (currentActivityName != ActivityName.CALENDAR) {
//                            val intent = Intent(context, CalendarActivity::class.java)
//                            context.startActivity(intent)
//                            activity.overridePendingTransition(0, 0)
                        }
                        true
                    }

                    else -> false
                }
            }
        }

        /**
         * Handles top app bar bar on every page page that has it.
         * Each activity must have a AppBarLayout with an MaterialToolbar using he "topAppBar" as its id or it will crash.
         */
        fun handleTopAppBar(view: View, context: Context, layoutInflater: LayoutInflater) {
            val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar);

            //Set the name of the profile to the first sub menu item
            val subProfile = topAppBar.menu.getItem(0).subMenu?.getItem(0)
            if (subProfile != null) {
                subProfile.title =
                    UserProfile.userProfile.firstName + " " + UserProfile.userProfile.lastName
            }


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
                        val dialog = builder.create()
                        dialog.show()

                        val dateOfBirth =
                            dialogView.findViewById<View>(R.id.dateOfBirth) as TextView
                        dateOfBirth.setOnClickListener { initCalendar(context, dateOfBirth) }

                        // Handle more events for the edit profile UI here

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