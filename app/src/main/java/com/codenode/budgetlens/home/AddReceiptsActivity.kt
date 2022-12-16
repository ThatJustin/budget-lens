package com.codenode.budgetlens.home

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
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


    //    val filledButton = findViewById<Button>(com.codenode.budgetlens.R.id.filledButton)
        filledButton.setOnClickListener {
            val drawable =resources.getDrawable(
                R.mipmap.ic_no
            )
            // 这一步必须要做，否则不会显示。
            drawable.setBounds(
                0, 0, drawable.minimumWidth,
                drawable.minimumHeight
            )
            if (TextUtils.isEmpty(tx1.text.toString().trim())){
                tx1.setCompoundDrawables(null, null, drawable, null)
            }
            if (TextUtils.isEmpty(tx3.text.toString().trim())){
                tx3.setCompoundDrawables(null, null, drawable, null)
            }
            if (TextUtils.isEmpty(tx4.text.toString().trim())){
                tx4.setCompoundDrawables(null, null, drawable, null)
            }
            if (TextUtils.isEmpty(tx5.text.toString().trim())){
                tx5.setCompoundDrawables(null, null, drawable, null)
            }
            if (TextUtils.isEmpty(tx6.text.toString().trim())){
                tx6.setCompoundDrawables(null, null, drawable, null)
            }
            if (TextUtils.isEmpty(tx7.text.toString().trim())){
                tx7.setCompoundDrawables(null, null, drawable, null)
            }
            if (TextUtils.isEmpty(txt1.text.toString().trim())){
                txt1.setCompoundDrawables(null, null, drawable, null)
            }
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

            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
               // .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .imageFormat(PictureMimeType.ofPNG())
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isEnableCrop(false)
                .isCompress(true)
                .isGif(false)// 是否显示gif图片 true or false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .isCamera(true)// 是否压缩
                .forResult(100);//结果回调onActivityResult code
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode!=0){
            return
        }

        if (resultCode == RESULT_OK) {
         var selectList = PictureSelector . obtainMultipleResult (data);
            file =   File(selectList.get(0).path);
            Log.e("onAcityResult", "Result:" + file)
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
        if (file==null){
            Toast.makeText(this, "Please choose img", Toast.LENGTH_SHORT).show()
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