package com.codenode.budgetlens.budget

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.fastjson.JSON
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.ExpandableHeightGridView
import com.codenode.budgetlens.R
import com.codenode.budgetlens.adapter.CostsAdapter
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.Costs
import com.codenode.budgetlens.data.Trend
import com.codenode.budgetlens.data.Tyepnames
import com.codenode.budgetlens.items.ItemsListPageActivity
import com.codenode.budgetlens.utils.AppUtils
import com.codenode.budgetlens.utils.HttpUtils
import com.codenode.budgetlens.utils.MyDialog
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_budget_page.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BudgetPageActivity : AppCompatActivity() {
    private var mMyDialog: MyDialog? = null
    var adapter: CategoryAdapter? = null
    var trendList = arrayListOf<Trend>()
    var grid :ExpandableHeightGridView?=null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_page)
        val textView6 = findViewById<TextView>(R.id.textView6)
        val spendingTrends = findViewById<TextView>(R.id.textView10)
        val goToItemsListActivity = Intent(this, ItemsListPageActivity::class.java)
        val time = System.currentTimeMillis()

        val format = SimpleDateFormat("MM", Locale.ENGLISH)
        val d1 = Date(time)
        val t1 = format.format(d1)

        val formats = SimpleDateFormat("dd", Locale.ENGLISH)
        val d1s = Date(time)
        val t1s = formats.format(d1s)

        AppUtils.setData(textView6, t1, t1s)
        getCosts()
        Log.e("WWWWWWWW", "$t1===$t1s")

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.BUDGET, this, this.window.decorView)
        CommonComponents.handleScanningReceipts(this.window.decorView, this, ActivityName.BUDGET)
        findViewById<ConstraintLayout>(R.id.constraintLayout3).setOnClickListener {
            val intent = Intent(this, BudgetSpendingTrendActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.clicktoadd).setOnClickListener {
            mMyDialog?.show()
        }

        grid = findViewById(R.id.grid_view)

        val screenWidth = windowManager.defaultDisplay.width
        val view1: View = layoutInflater.inflate(R.layout.dialog_add, null)
        mMyDialog = MyDialog(this, view1, true, true)
        val mainLayout: LinearLayout = view1.findViewById(R.id.mainlayout)
        val lp: ViewGroup.LayoutParams = mainLayout.layoutParams
        lp.width = screenWidth
        mainLayout.layoutParams = lp
        val name = view1.findViewById<EditText>(R.id.tv_context)
        val box = view1.findViewById<CheckBox>(R.id.tv_box)
        view1.findViewById<TextView>(R.id.tv_sure).setOnClickListener {
            if (TextUtils.isEmpty(name.text.toString().trim())){
                Toast.makeText(
                    this@BudgetPageActivity,
                    "No input, please enter a valid category name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            addType(name.text.toString().trim(), box.isChecked)
            mMyDialog?.dismiss()
            name.setText("")
            box.isChecked=false
        }

        getCostData()
        spendingTrends.setOnClickListener {
            startActivity(goToItemsListActivity)
            }
    }

    fun addAdapter() {
        if (trendList.size > 0) {
            tip.visibility=View.GONE
            tv_ts.visibility=View.VISIBLE
        }else{
            tip.visibility=View.VISIBLE
            tv_ts.visibility=View.GONE
        }
        adapter = CategoryAdapter(this,responseSt, trendList)

        grid!!.isFocusable = false
        grid!!.isExpanded = true
        grid!!.adapter = adapter
    }

    private fun addType(name: String, star: Boolean) {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"
        val params = RequestParams()

        params.put("category_name", name)
        params.put("category_toggle_star", star)
        params.put("parent_category_id", "")
        params.put("icon", "")
        HttpUtils.post("Bearer ${BearerToken.getToken(this)}", url, params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                Toast.makeText(
                    this@BudgetPageActivity,
                    "Failed to add a category, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                Toast.makeText(
                    this@BudgetPageActivity,
                    "Successful",
                    Toast.LENGTH_SHORT
                ).show()

                getCostData()
                Log.i("Response", "responseString$responseString")
            }
        })
    }

    private fun getCosts() {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/costs/"
        HttpUtils.get("Bearer ${BearerToken.getToken(this)}", url, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                val bean=JSON.parseObject(responseString, Costs::class.java)
                val madapter = CostsAdapter()

                recyc.adapter = madapter
                madapter.setNewInstance(bean.Costs as MutableList<Costs.CostsBean>?)

                val list: MutableList<Float> = ArrayList()
                for (fl in bean.Costs!!) {
                    list.add(fl.category_cost!!)
                }
                val data = list.toTypedArray()
                pieChart.setDatas(data)
            }
        })
    }

    var responseSt=""
    private fun getCostData() {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/items/category/costs/date/days=90/"
        HttpUtils.get("Bearer ${BearerToken.getToken(this)}", url, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {
                responseSt=responseString
                getType()
            }
        })
    }

    fun getType() {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"

        Log.i("Response", "aaa$url")

        val registrationPost = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()
        ("").trimIndent().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .build()

        registrationPost.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            Log.i("Successful", "Logout Successfully.$responseBody")
                            val list = JSON.parseArray(responseBody, Tyepnames::class.java)

                            trendList = arrayListOf()

                            Log.e("Response", "aaa$list")

                            for (type in list) {
                                if(type.category_toggle_star){
                                    val trend = Trend()
                                    trend.icon = R.drawable.restaurant
                                    trend.name = type.category_name
                                    trendList.add(trend)
                                }
                            }

                            grid!!.post { addAdapter() }
                        } else {
                            Log.i("Empty", "Something went wrong${response.body?.string()}")
                        }
                    } else {
                        Log.e(
                            "Error",
                            "Something went wrong${response.body?.string()} ${response.message} ${response.headers}"
                        )
                    }
                }
            }
        })
    }
}