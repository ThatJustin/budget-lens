package com.codenode.budgetlens.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.common.ScanningReceiptActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class HomePageActivity() : AppCompatActivity() {


    private var AddMenuIsClosed:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

//        CommonComponents.handleAddReceipt(this.window.decorView, this, layoutInflater)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)

        onBackPressedDispatcher.addCallback(
            this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    moveTaskToBack(true)
                }
            })

        val openAddMenu: FloatingActionButton = findViewById(R.id.addReceipts)
        val manualReceiptButton:FloatingActionButton = findViewById(R.id.createManual)
        val scanReceiptButton:FloatingActionButton = findViewById(R.id.ScanReceipt)

        openAddMenu.setOnClickListener{
            if(AddMenuIsClosed){
                openAddMenu.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                AddMenuIsClosed = false
                manualReceiptButton.isEnabled = true
                manualReceiptButton.alpha = 1.0F
                scanReceiptButton.isEnabled = true
                scanReceiptButton.alpha = 1.0F

            }else{
                openAddMenu.setImageResource(R.drawable.ic_baseline_add_24)
                AddMenuIsClosed = true
                manualReceiptButton.isEnabled = false
                manualReceiptButton.alpha = 0.0F
                scanReceiptButton.isEnabled = false
                scanReceiptButton.alpha = 0.0F
            }
        }

        scanReceiptButton.setOnClickListener{
            val intent = Intent(this, ScanningReceiptActivity::class.java)
            openAddMenu.setImageResource(R.drawable.ic_baseline_add_24)
            manualReceiptButton.isEnabled = false
            manualReceiptButton.alpha = 0.0F
            scanReceiptButton.isEnabled = false
            scanReceiptButton.alpha = 0.0F
            this.startActivity(intent)
        }


    }
}