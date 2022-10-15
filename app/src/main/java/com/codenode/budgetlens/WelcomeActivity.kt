package com.codenode.budgetlens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val actionBar = supportActionBar

        actionBar!!.title = "Sign Out"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}