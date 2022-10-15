package com.codenode.budgetlens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val LoginActivityBtn: ImageButton = findViewById(R.id.LoginActivityBtn)
        val actionBar = supportActionBar

        actionBar!!.title = "Welcome to BudgetLens"
        LoginActivityBtn.setOnClickListener(){
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }
}