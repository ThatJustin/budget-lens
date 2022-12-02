package com.codenode.budgetlens.category

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import com.codenode.budgetlens.R
import org.xmlpull.v1.XmlPullParserFactory

class AddSubCategoryPopUpActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sub_category_pop_up)

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width * 0.7).toInt(), (height * 0.5).toInt())
//        window.setBackgroundBlurRadius(100)
//        window.setBackgroundDrawableResource(R.drawable.pop_up)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }
}