package com.codenode.budgetlens.common

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
                        var firstName = dialogView.findViewById<View>(R.id.firstName) as EditText
                        var lastName = dialogView.findViewById<View>(R.id.lastName) as EditText
                        var email = dialogView.findViewById<View>(R.id.email) as EditText
                        var phone = dialogView.findViewById<View>(R.id.phone) as EditText
                        val dateOfBirth =
                            dialogView.findViewById<View>(R.id.dateOfBirth) as TextView
                        dateOfBirth.setOnClickListener { initCalendar(context, dateOfBirth) }

                        val prefs=context.getSharedPreferences("data",Context.MODE_PRIVATE);
                        val firstNameShared=prefs.getString("firstName","John");
                        val lastNameShared=prefs.getString("lastName","Smith");
                        var phoneShared = prefs.getString("phone","5141111111");
                        var emailShared = prefs.getString("email","johncena123@gmail.com");
                        var dateOfBirthShared = prefs.getString("dateOfBirth","2000/09/04");
                        firstName.setText(firstNameShared)
                        lastName.setText(lastNameShared)
                        phone.setText(phoneShared)
                        email.setText(emailShared)
                        dateOfBirth.setText(dateOfBirthShared);
                        // Handle more events for the edit profile UI here
                        var confirm = dialogView.findViewById<View>(R.id.confirm) as Button
                        confirm.setOnClickListener{
                            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT)
                                .show()
                            val sp = prefs.edit()
                            UserProfile.updateProfile(false,
                                firstName.text.toString()+lastName.text.toString(),
                                firstName.text.toString(),
                                lastName.text.toString(),
                                email.text.toString(),
                                phone.text.toString(),
                                dateOfBirth.text.toString(),
                                context
                            )
                            dialog.dismiss();
                        }
//                        confirm.setOnClickListener{
//                            UserProfile.updateProfile(
//                                false,
//                                firstName.text.toString()+lastName.text.toString(),
//                                firstName.text.toString()+"",
//                                lastName.text.toString()+"",
//                                email.text.toString()+"",
//                                phone.text.toString()+"",
//                                dateOfBirth.text.toString() +"",
//                                context
//                            )
//                        }
                        //var
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