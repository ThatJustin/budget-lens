package com.codenode.budgetlens.calendar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.adapter.CalendarAdapter
import com.codenode.budgetlens.common.ActivityName
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.CommonComponents
import com.codenode.budgetlens.data.CalendarBean
import com.codenode.budgetlens.utils.HttpUtils
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_calendar_list.*
import java.math.BigDecimal


class CalendarListActivity : AppCompatActivity() {

    var madapter= CalendarAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_list)
        CommonComponents.handleTopAppBar(this.window.decorView, this, layoutInflater)
        CommonComponents.handleNavigationBar(ActivityName.HOME, this, this.window.decorView)
       /* tv_split.setOnClickListener {

        }*/
        friends_list.adapter=madapter
        getData()
        madapter.setOnItemChildClickListener { adapter, view, position ->
            startActivity(Intent(this, ChooseFriendActivity::class.java))
        }
        tv_cancel.setOnClickListener {
            finish()
        }
        tv_sure.setOnClickListener {
            val intent = Intent()
            setResult(101, intent)
            finish()
        }
    }

    fun add(v1: String?, v2: String?): String? {
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        return b1.add(b2).toString()
    }
    private fun getData() {

        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/itemsplit/sharedAmountList/"
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
                var bean= JSON.parseObject(responseString, CalendarBean::class.java);

                madapter.setNewInstance(bean.data as MutableList<CalendarBean.DataDTO>?)
                var num="0"
                for (str in bean.data!!) {
                    num=add(num,str.item_price).toString()
                }
                tv_num.setText("$"+num)
            }
        })
    }
}