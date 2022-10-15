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

        val myBottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        myBottomNavigationView.setOnItemSelectedListener {  item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    // Respond to navigation item 1 click
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                    true
                }
                R.id.page_2 -> {
                    // Respond to navigation item 2 click
//                    val intent = Intent(this, SignUpActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                    true
                }
                R.id.page_3 -> {
                // Respond to navigation item 2 click
//                    val intent = Intent(this, SignUpActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                true
                }
                R.id.page_4 -> {
                // Respond to navigation item 2 click
//                    val intent = Intent(this, SignUpActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                true
                }R.id.page_5 -> {
                // Respond to navigation item 2 click
//                    val intent = Intent(this, SignUpActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(0,0)
                true
                }

                else -> false
            }
        }
    }
}