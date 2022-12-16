package com.codenode.budgetlens.budget

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
    var mMyDialog: MyDialog? = null
    var adapter: CategoryAdapter? = null
    var trendlist = arrayListOf<Trend>()
    var grid :ExpandableHeightGridView?=null
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_page)
        var textView6 = findViewById<TextView>(R.id.textView6);
        val time = System.currentTimeMillis() //long now = android.os.SystemClock.uptimeMillis();

        val format = SimpleDateFormat("MM")
        val d1 = Date(time)
        val t1 = format.format(d1)

        val formats = SimpleDateFormat("dd")
        val d1s = Date(time)
        val t1s = formats.format(d1s)

        AppUtils.setData(textView6, t1, t1s)
        getCosts()
        Log.e("aaa", t1 + "===" + t1s)

        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.BUDGET, this, this.window.decorView)
        findViewById<ConstraintLayout>(R.id.constraintLayout3).setOnClickListener() {
            val intent = Intent(this, BudgetSpendingTrendActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.clicktoadd).setOnClickListener() {
            mMyDialog?.show()
        }

        grid = findViewById<ExpandableHeightGridView>(R.id.grid_view)
        /*  var trend = Trend()
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
          trendlist.add(trend)*/
        //AddAdapter()

        val screenWidth = windowManager.defaultDisplay.width // the width of the screen
        val view1: View = layoutInflater.inflate(R.layout.dialog_add, null)
        mMyDialog = MyDialog(this, view1, true, true)
        val mainLayout: LinearLayout = view1.findViewById(R.id.mainlayout)
        val lp: ViewGroup.LayoutParams = mainLayout.getLayoutParams()
        lp.width = screenWidth
        mainLayout.setLayoutParams(lp)
        var name = view1.findViewById<EditText>(R.id.tv_context)
        var box = view1.findViewById<CheckBox>(R.id.tv_box)
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
        getType()
    }

    fun AddAdapter() {

        Log.e("Response", "trendlistr---"+trendlist.toString())
        adapter = CategoryAdapter(this, trendlist)
        grid!!.isFocusable = false;
        grid!!.isExpanded = true
        grid!!.adapter = adapter
    }


    private fun addType(name: String, star: Boolean) {

        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"
        val params = RequestParams()

        params.put("category_name", name)
        params.put("category_toggle_star", star)
        params.put("parent_category_id", "")
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

                getType()
                Log.i("Response", "responseString"+responseString)
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
                var bean=JSON.parseObject(responseString, Costs::class.java);


                var madapte= CostsAdapter()
                recyc.adapter=madapte;
                madapte.setNewInstance(bean.Costs as MutableList<Costs.CostsBean>?)
                val list: MutableList<Float> = ArrayList()
                for (fl in bean.Costs!!) {
                    list.add(fl.category_cost!!)
                }
                val datas = list.toTypedArray()
                pieChart.setDatas(datas)
            }
        })
    }

    fun getType() {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/category/"

        Log.i("Response", "aaa" + url)
        val registrationPost = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()

        val body = ("").trimIndent().toRequestBody(mediaType)

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
                            Log.i("Successful", "Logout Successfully."+responseBody)
                            var  list = JSON.parseArray(responseBody, Tyepnames::class.java)

                            trendlist = arrayListOf<Trend>()

                            Log.e("Response", "aaa"+list.toString())
                            for (type in list) {
                                if(type.category_toggle_star){
                                    var trend = Trend()
                                    trend.icon = R.drawable.restaurant
                                    trend.name = type.category_name
                                    trendlist.add(trend)
                                }

                            }
                            grid!!.post { AddAdapter() }

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