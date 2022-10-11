package com.codenode.budgetlens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val actionBar = supportActionBar

        actionBar!!.title = "Login"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}