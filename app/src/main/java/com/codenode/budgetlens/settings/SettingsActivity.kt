package com.codenode.budgetlens.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import com.codenode.budgetlens.R
import com.codenode.budgetlens.category.CategoryActivity
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.home.HomePageActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var categoryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.SETTINGS, this, this.window.decorView)
        CommonComponents.handleScanningReceipts(this.window.decorView, this, ActivityName.SETTINGS)

//        When clicking on the Category button, go to the Category Setting page.
        categoryButton = findViewById(R.id.category)
        val goToCategorySettingsPage = Intent(this, CategoryActivity::class.java)
        categoryButton.setOnClickListener{
            startActivity(goToCategorySettingsPage)
        }
    }
}