package com.codenode.budgetlens.budget

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Trend

class BudgetSpendingTrendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_spending_trend)
        var layoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

        var trendList = findViewById<RecyclerView>(R.id.trendlist)
        trendList.layoutManager = layoutManager

        val trendlist = arrayListOf<Trend>()
        var trend = Trend()
        trend.icon = R.drawable.grocery
        trend.name = "Grocery"
        trendlist.add(trend)

        trend = Trend()
        trend.icon = R.drawable.clothing
        trend.name = "Clothing"
        trendlist.add(trend)

        val adapter = TrendAdapter(trendlist, baseContext)
        trendList.adapter = adapter
    }
}