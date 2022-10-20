package com.codenode.budgetlens.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.CommonComponents
import com.google.android.material.bottomnavigation.BottomNavigationView


open class HomePageActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        CommonComponents.handleNavigationBar(this.window.decorView)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
    }
}