package com.codenode.budgetlens.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.budget.BudgetPageActivity
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.common.ScanningReceiptActivity
import com.codenode.budgetlens.data.UserProfile
import com.codenode.budgetlens.login.LoginActivity
import com.codenode.budgetlens.manualReceipt.Receipt
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_home_page.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

open class HomePageActivity() : AppCompatActivity() {


    private var AddMenuIsClosed:Boolean = true
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

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

        val context = this

        manualReceiptButton.setOnClickListener {
            startActivity(Intent(this,AddReceiptsActivity::class.java))
        }

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

    override fun onStart() {
        super.onStart()
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)
    }
}