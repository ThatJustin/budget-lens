package com.codenode.budgetlens.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.receipts.ReceiptsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

open class HomePageActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)

        onBackPressedDispatcher.addCallback(
            this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    moveTaskToBack(true)
                }
            })
        loadFragment(ReceiptsFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setSelectedItemId(R.id.receipt);

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    Toast.makeText(this@HomePageActivity, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.receipt -> {
                    Toast.makeText(this@HomePageActivity, "Receipt", Toast.LENGTH_SHORT).show()
                    loadFragment(ReceiptsFragment())
                }
                R.id.budget -> {
                    Toast.makeText(this@HomePageActivity, "Budget", Toast.LENGTH_SHORT).show()
                }
                R.id.friends -> {
                    Toast.makeText(this@HomePageActivity, "Friends", Toast.LENGTH_SHORT).show()
                }
                R.id.calendar -> {
                    Toast.makeText(this@HomePageActivity, "Calendar", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@HomePageActivity, "else", Toast.LENGTH_SHORT).show()

                }
            }
            true
        }

    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}