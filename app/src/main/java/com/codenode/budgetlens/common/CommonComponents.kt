package com.codenode.budgetlens.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.UserProfile
import com.codenode.budgetlens.home.HomePageActivity
import com.codenode.budgetlens.manualReceipt.Receipt
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class CommonComponents {
    companion object {

        /**
         * Handles navigation bar on every page page that has it.
         * Each activity must have a BottomNavigationView with the "bottom_navigation" as its id or it will crash.
         */
        fun handleNavigationBar(
            currentActivityName: ActivityName,
            context: Context,
            view: View
        ) {
            val activity: Activity = context as Activity

            val myBottomNavigationView =
                view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

            myBottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        println("Calling handletopbar")
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
                            showPopup(myBottomNavigationView,context,activity)
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

        private fun showPopup(view: View,context: Context,activity:Activity) {
            var popup: PopupMenu? = null;
            popup = PopupMenu(context, view)
            popup.inflate(R.menu.popup_menu)
            popup.setGravity(Gravity.END);

            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.item1 -> {
                        Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show();
                    }
                    R.id.item2 -> {
                        //Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show();
                        //val goReceiptPageActivity = Intent(context, Receipt::class.java)
                        val intent = Intent(context, Receipt::class.java)
                        context.startActivity(intent)
                        activity.overridePendingTransition(0, 0)
                    }
                }

                true
            })

            popup.show()
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

                        val firstName =
                            dialogView.findViewById<View>(R.id.firstName) as EditText
                        val lastName = dialogView.findViewById<View>(R.id.lastName) as EditText
                        val email = dialogView.findViewById<View>(R.id.email) as EditText
                        val phone = dialogView.findViewById<View>(R.id.phone) as EditText

                        firstName.setText(UserProfile.userProfile.firstName)
                        lastName.setText(UserProfile.userProfile.lastName)
                        phone.setText(UserProfile.userProfile.telephoneNumber)
                        email.setText(UserProfile.userProfile.email)

                        // Handle more events for the edit profile UI here
                        val confirm = dialogView.findViewById<View>(R.id.confirm) as Button
                        confirm.setOnClickListener {
                            UserProfile.updateProfile(
                                true,
                                email.text.toString(),
                                firstName.text.toString(),
                                lastName.text.toString(),
                                email.text.toString(),
                                phone.text.toString(),
                                context
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
}