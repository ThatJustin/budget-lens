package com.codenode.budgetlens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        navigationBarControl();


    }

    private fun navigationBarControl() {
        val myBottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

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
}