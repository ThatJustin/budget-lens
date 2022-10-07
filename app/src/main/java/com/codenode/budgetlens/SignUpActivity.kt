package com.codenode.budgetlens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val actionBar = supportActionBar

        actionBar!!.title = "Sign Up"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}