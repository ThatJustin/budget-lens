package com.codenode.budgetlens.budget

import android.content.Intent
import android.view.View
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.codenode.budgetlens.ExpandableHeightGridView
import com.codenode.budgetlens.PieChartView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Trend

class BudgetPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_page)
        val piechart = findViewById<PieChartView>(R.id.pieChart)
        val datas = arrayOf(70f, 80.0f, 90.00f, 103f, 120f,77f)
        piechart.setDatas(datas)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.BUDGET, this, this.window.decorView)
        findViewById<ConstraintLayout>(R.id.constraintLayout3).setOnClickListener(){
            val intent = Intent(this, BudgetSpendingTrendActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.clicktoadd).setOnClickListener(){
            findViewById<TextView>(R.id.clicktoadd).visibility = View.GONE
            findViewById<TextView>(R.id.tip).visibility = View.GONE
            findViewById<GridView>(R.id.grid_view).visibility = View.VISIBLE
        }

        val trendlist = arrayListOf<Trend>()
        var trend = Trend()
        trend.icon = R.drawable.restaurant
        trend.name = "Restaurant"
        trendlist.add(trend)

        trend = Trend()
        trend.icon = R.drawable.bar
        trend.name = "Bar"
        trendlist.add(trend)

        trend = Trend()
        trend.icon = R.drawable.clothing
        trend.name = "Clothing"
        trendlist.add(trend)

        trend = Trend()
        trend.icon = R.drawable.grocery
        trend.name = "Grocery"
        trendlist.add(trend)

        val adapter = CategoryAdapter(this, trendlist)
        val grid = findViewById<ExpandableHeightGridView>(R.id.grid_view)
        grid.isFocusable = false;
        grid.isExpanded = true
        grid.adapter = adapter
    }
}