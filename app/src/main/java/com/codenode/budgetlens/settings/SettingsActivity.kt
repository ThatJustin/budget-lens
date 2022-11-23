package com.codenode.budgetlens.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.SETTINGS, this, this.window.decorView)
//
//        onBackPressedDispatcher.addCallback(
//            this /* lifecycle owner */,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    moveTaskToBack(true)
//                }
//            })
    }
}