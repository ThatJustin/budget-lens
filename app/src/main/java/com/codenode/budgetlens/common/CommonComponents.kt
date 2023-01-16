package com.codenode.budgetlens.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.budget.BudgetPageActivity
import com.codenode.budgetlens.calendar.CalendarListActivity
import com.codenode.budgetlens.data.UserProfile
import com.codenode.budgetlens.friends.FriendsPageActivity
import com.codenode.budgetlens.home.AddReceiptsActivity
import com.codenode.budgetlens.home.HomePageActivity
import com.codenode.budgetlens.items.ItemsListPageActivity
import com.codenode.budgetlens.login.LoginActivity
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity
import com.codenode.budgetlens.settings.SettingsActivity
import com.codenode.budgetlens.utils.AppUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CommonComponents {
    companion object {

        fun handleScanningReceipts(
            view: View,
            context: Context,
            currentActivityName: ActivityName){
            var addReceiptInfoButton = false
            val openAddMenu: FloatingActionButton = view.findViewById(R.id.addReceipts)
            val manualReceiptButton:FloatingActionButton = view.findViewById(R.id.createManual)
            val scanReceiptButton:FloatingActionButton = view.findViewById(R.id.ScanReceipt)
            val activity: Activity = context as Activity

            manualReceiptButton.setOnClickListener {
                AppUtils.changeActivities(activity, AddReceiptsActivity::class.java, 0, 0)
            }

            openAddMenu.setOnClickListener{
                if(!addReceiptInfoButton){
                    openAddMenu.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    addReceiptInfoButton = true
                    if (currentActivityName == ActivityName.SCAN) {
                        manualReceiptButton.isEnabled = true
                        manualReceiptButton.alpha = 1.0F
                        scanReceiptButton.isEnabled = false
                        scanReceiptButton.alpha = 0.0F
                    }else if (currentActivityName == ActivityName.MANUAL_RECEIPTS){
                        manualReceiptButton.isEnabled = false
                        manualReceiptButton.alpha = 0.0F
                        scanReceiptButton.isEnabled = true
                        scanReceiptButton.alpha = 1.0F
                    }else{
                        manualReceiptButton.isEnabled = true
                        manualReceiptButton.alpha = 1.0F
                        scanReceiptButton.isEnabled = true
                        scanReceiptButton.alpha = 1.0F

                    }
                    scanReceiptButton.isEnabled = true
                    scanReceiptButton.alpha = 1.0F

                }else{
                    openAddMenu.setImageResource(R.drawable.ic_baseline_add_24)
                    addReceiptInfoButton = false
                    manualReceiptButton.isEnabled = false
                    manualReceiptButton.alpha = 0.0F
                    scanReceiptButton.isEnabled = false
                    scanReceiptButton.alpha = 0.0F
                }
            }

            scanReceiptButton.setOnClickListener{
                openAddMenu.setImageResource(R.drawable.ic_baseline_add_24)
                manualReceiptButton.isEnabled = false
                manualReceiptButton.alpha = 0.0F
                scanReceiptButton.isEnabled = false
                scanReceiptButton.alpha = 0.0F
                AppUtils.changeActivity(activity, ScanningReceiptActivity::class.java, 0, 0)
            }

        }

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

            //Handles the floating budget button
            view.findViewById<FloatingActionButton>(R.id.fabut)?.setOnClickListener {
                AppUtils.changeActivity(activity, BudgetPageActivity::class.java, 0, 0)
            }

            //handles bottom navbar navigation
            myBottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        if (currentActivityName != ActivityName.HOME) {
                            AppUtils.changeActivity(activity, HomePageActivity::class.java, 0, 0)
                        }
                        true
                    }
                    R.id.receipts -> {
                        if (currentActivityName != ActivityName.RECEIPTS) {
                            AppUtils.changeActivity(
                                activity,
                                ReceiptsListPageActivity::class.java,
                                0,
                                0
                            )
                        }
                        true
                    }
                    R.id.budget -> {
                        if (currentActivityName != ActivityName.BUDGET) {
                            AppUtils.changeActivity(activity, BudgetPageActivity::class.java, 0, 0)
                        }
                        true
                    }
                    R.id.friends -> {
                        if (currentActivityName != ActivityName.FRIENDS) {
                            AppUtils.changeActivity(activity, FriendsPageActivity::class.java, 0, 0)
                        }
                        true
                    }
                    R.id.items -> {
                        if (currentActivityName != ActivityName.ITEMS) {
                            AppUtils.changeActivity(activity, ItemsListPageActivity::class.java, 0, 0)
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
            val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
            //Set the name of the profile to the first sub menu item
            if (topAppBar.menu.size()>0){
                val subProfile = topAppBar.menu.getItem(0).subMenu?.getItem(0)
                if (subProfile != null) {
                    subProfile.title =
                        UserProfile.userProfile.firstName + " " + UserProfile.userProfile.lastName
                }
            }


            topAppBar.setNavigationOnClickListener {
                // Handle navigation icon press
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_icon -> {
                        //Do nothing
                        if (subProfile != null) {
                            subProfile.title =
                                UserProfile.userProfile.firstName + " " + UserProfile.userProfile.lastName
                        }

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
                        if(!dialog.isShowing){

                            dialog.show()
                        }

                        val firstName =
                            dialogView.findViewById<View>(R.id.firstName_edit) as EditText
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
                                context,
                                dialog
                            )
                        }
                        true
                    }
                    R.id.settings -> {
                        val intent: Intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                        BearerToken.getToken(context)
                        true
                    }
                    R.id.logout -> {
                        requestLogoutAPI(context)
                        true
                    }
                    else -> false
                }
            }
        }

        private fun requestLogoutAPI(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)

            val url =
                "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/logoutEndpoint/"

            val registrationPost = OkHttpClient()
            val mediaType = "application/json".toMediaTypeOrNull()
            val body = ("").trimIndent().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .method("DELETE", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .build()

            registrationPost.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                Log.i("Successful", "Logout Successful.")
                                BearerToken.delete(context)
                                context.startActivity(intent)
                            } else {
                                Log.i(
                                    "Empty",
                                    "Something went wrong${response.body?.string()}"
                                )
                            }
                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong${response.body?.string()} ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }
    }
}