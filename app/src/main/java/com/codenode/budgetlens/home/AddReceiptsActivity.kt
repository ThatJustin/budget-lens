package com.codenode.budgetlens.home

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.data.Merchant
import com.codenode.budgetlens.utils.CustomBubbleAttachPopup
import com.codenode.budgetlens.utils.HttpUtils
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_add_receipts.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*


class AddReceiptsActivity : AppCompatActivity() {
    var ctx = this;
    var popview: BasePopupView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_receipts)
        EventBus.getDefault().register(this);
        val date = findViewById<TextView>(R.id.tx1)
        date.setOnClickListener {
            initCalendar(this, date)
        }


        val filledButton = findViewById<Button>(com.codenode.budgetlens.R.id.filledButton)
        filledButton.setOnClickListener {

            Submit()
        }
        tvMercant.setOnClickListener {

            getType()

        }
        tvCurrency.setOnClickListener {

            var pop = CustomBubbleAttachPopup(this);
            popview = XPopup.Builder(this) //
                // .isCenterHorizontal(true)
                // .isDestroyOnDismiss(true)
                .atView(tvCurrency)
                .hasShadowBg(false) // delete the half-transparent backend
                .asCustom(
                    pop
                )
                .show()
            val list: MutableList<String> = ArrayList()
            list.add("USD")
            list.add("CAD")
            val screenWidth: Int = getWindowManager().getDefaultDisplay().getWidth() // the width of the screen

            Log.e("ddd", "onEv111ent: " + list.size)
            pop.addData(list, 2, screenWidth * 5 / 6)

        }
        tx2.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, 2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 ) {
            Log.e("eee", "Result:" + data!!.data!!.path)
            file =   File(data!!.data!!.path);
            Log.e("eee", "Result:" + file)

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(message: Message) {
        if (message.arg1 === 1) {

            if (TextUtils.equals(message.obj.toString(), "add")) {
                XPopup.Builder(this)
                    .autoOpenSoftInput(true)
                    .isDestroyOnDismiss(true)
                    .asInputConfirm(
                        "add", null, "", "add",
                        {
                            if (TextUtils.isEmpty(it)) {
                                Toast.makeText(
                                    this,
                                    "Please enter the name for the merchant",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@asInputConfirm
                            }
                            AddType(it)
                            Log.e("ddd", "onEvent: " + it)
                        }, null, R.layout.pop_confim_popup
                    )
                    .show()
            } else {
                tvMercant.text = message.obj.toString()
                merchant = message.arg2
            }

            popview?.dismiss()
        } else if (message.arg1 === 2) {
            tvCurrency.text = message.obj.toString()
            popview?.dismiss()
        }
    }

    var file : File?=null
    var merchant = 0
    private fun Submit() {
        if (merchant == 0) {
            Toast.makeText(this, "Please choose the merchant", Toast.LENGTH_SHORT).show()

            return
        }
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/"
        val params = RequestParams()
        params.put("merchant", merchant)
        params.put("scan_date", tx1.text.toString())
        params.put("receipt_image", file)
        params.put("location", tx3.text.toString())
        params.put("total", tx4.text.toString())
        params.put("tax", tx5.text.toString())
        params.put("tip", tx6.text.toString())
        params.put("coupon", tx7.text.toString())
        params.put("currency", tvCurrency.text.toString())
        params.put("receipt_text", txt1.text.toString())
        HttpUtils.post(
            "Bearer ${BearerToken.getToken(this)}",
            url,
            params,
            object : TextHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String,
                    throwable: Throwable
                ) {
                    Log.i("ccc", "responseString" + responseString)
                    Log.i("ccc", "responseString" + throwable.toString())

                    Toast.makeText(
                        this@AddReceiptsActivity,
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
                        this@AddReceiptsActivity,
                        "Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    Log.i("Response", "responseString" + responseString)
                }
            })
    }

    private fun AddType(name: String) {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/merchant/"
        val params = RequestParams()

        params.put("name", name)
        HttpUtils.post(
            "Bearer ${BearerToken.getToken(this)}",
            url,
            params,
            object : TextHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String,
                    throwable: Throwable
                ) {
                    Toast.makeText(
                        this@AddReceiptsActivity,
                        "Failed to add a category, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String
                ) {


                    Log.e("aaa", "onSuccess: " + responseString)
                }
            })
    }

    private fun getType() {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/merchant/"
        // val params = RequestParams()

        // params.put("merchant", name)
        HttpUtils.get(
            "Bearer ${BearerToken.getToken(this)}",
            url,
            object : TextHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String,
                    throwable: Throwable
                ) {
                    Toast.makeText(
                        this@AddReceiptsActivity,
                        "Failed to add a category, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String
                ) {
                    var bean = JSON.parseObject(responseString, Merchant::class.java)

                    val list: MutableList<String?> = ArrayList()

                    Log.e("aaa", "responseString: " + responseString)
                    Log.e("aaa", "onSuccess: " + bean.toString())
                    for (str in bean.getMerchants()!!) {

                        list.add(str!!.name!!)
                    }
                    showPop(list)
                }
            })
    }

    fun showPop(list: MutableList<String?> = ArrayList()) {

        var pop = CustomBubbleAttachPopup(this);
        popview = XPopup.Builder(this) //
            // .isCenterHorizontal(true)
            //.isDestroyOnDismiss(true)
            .atView(tvMercant)
            .hasShadowBg(false)
            .asCustom(
                pop
            )
            .show()
        list.add("add")
        val screenWidth: Int = getWindowManager().getDefaultDisplay().getWidth()

        Log.e("bbb", "onEvent: " + list.size)
        pop.addData(list, 1, screenWidth * 5 / 6)
    }

    private fun initCalendar(context: Context, textView: TextView) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            context,
            { view, year, month, dayOfMonth ->
                val text = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                textView.text = text
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        dialog.show()
    }
}