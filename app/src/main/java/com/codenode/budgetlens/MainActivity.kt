package com.codenode.budgetlens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.budget.BudgetPageActivity
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.GlobalSharedPreferences
import com.codenode.budgetlens.login.LoginActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalSharedPreferences.load(this)
        
        //if they have a token, they can go to the home page
        val intent: Intent = if (BearerToken.exists(this)) {
            Intent(this, BudgetPageActivity::class.java)
        } else { //otherwise they must register or login
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        // removes this activity from the stack, prevents users from going back
        finish()
    }
}