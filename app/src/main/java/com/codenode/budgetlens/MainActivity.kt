package com.codenode.budgetlens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.view.View
import android.widget.RadioButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var home = findViewById<RadioButton>(R.id.home)
        var dashboard = findViewById<RadioButton>(R.id.dashboard)
        var notifications = findViewById<RadioButton>(R.id.notifications)
        var walk = findViewById<RadioButton>(R.id.walk)
        var profile = findViewById<RadioButton>(R.id.profile)

        val LoginActivityBtn: ImageButton = findViewById(R.id.LoginActivityBtn)
        val actionBar = supportActionBar

        actionBar!!.title = "Welcome to BudgetLens"
        LoginActivityBtn.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        dashboard.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            startActivity(intent)
        });
        notifications.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, NotificationActivity::class.java)
            startActivity(intent)
        });
        walk.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, WalkActivity::class.java)
            startActivity(intent)
        });
        profile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        });
    }
}