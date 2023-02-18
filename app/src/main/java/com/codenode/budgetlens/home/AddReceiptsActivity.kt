package com.codenode.budgetlens.home


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.data.Merchant
import com.codenode.budgetlens.utils.CustomBubbleAttachPopup
import com.codenode.budgetlens.utils.GlideEngine
import com.codenode.budgetlens.utils.HttpUtils
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_add_receipts.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.IOException
import java.util.*


class AddReceiptsActivity : AppCompatActivity() {
    var ctx = this;
    var path = "http://192.168.2.15:8000/media/uploads/lake-snow-Annecy-ice-clouds-montagne-1016845-wallhere.com_2bVhZU3.jpg"
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
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .imageSpanCount(4)
                .imageFormat(PictureMimeType.ofPNG())
                .selectionMode(PictureConfig.SINGLE)
                .isCamera(true)
                .isZoomAnim(true)
                .isEnableCrop(false)
                .isCompress(true)
                .isGif(false)
                .minimumCompressSize(10)
                .isPreviewEggs(true)
                .forResult(100);
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("dsadasd", "" + resultCode + "Submit: " + RESULT_OK)


        Log.e("dsadasd", "" + resultCode + "Submit: " + RESULT_OK)
        if (resultCode == RESULT_OK) {
            var selectList = PictureSelector.obtainMultipleResult(data);
            Log.e("dsadasd", "Submit: " + selectList.get(0).path)
            path = selectList.get(0).compressPath
            file = File(selectList.get(0).path);

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
        if (TextUtils.isEmpty( tx1.text.toString())){
            return
        }
       /* if (TextUtils.isEmpty( tx2.text.toString())){
            return
        }*/
        if (TextUtils.isEmpty( tx3.text.toString())){
            return
        }
        if (TextUtils.isEmpty( tx4.text.toString())){
            return
        }
        if (TextUtils.isEmpty( tx5.text.toString())){
            return
        }
        if (TextUtils.isEmpty( tx6.text.toString())){
            return
        }
        if (TextUtils.isEmpty( tx7.text.toString())){
            return
        }
        Log.e("dsadasd", "Submit: " + tx1.text.toString())
      /*  if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "Please choose img", Toast.LENGTH_SHORT).show()
            return
        }*/
        //getupload(path)
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/manualReceipts/"
        val params = RequestParams()
        params.put("merchant", merchant)
        params.put("scan_date", tx1.text.toString())
        params.put("user", BearerToken.getToken(this))
        params.put("receipt_image", path)
        params.put("location", tx3.text.toString())
        params.put("total", tx4.text.toString())
        params.put("tax", tx5.text.toString())
        params.put("tip", tx6.text.toString())
        params.put("coupon", tx7.text.toString())
        params.put("currency", tvCurrency.text.toString())
        params.put("receipt_text", txt1.text.toString())
        HttpUtils.posts(
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

    private val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()
    private val client: OkHttpClient = OkHttpClient()

    fun getupload(str: String?) {
        val file = File(str) //filePath address of the picture
         val token = "e8465a74d607fdffed3eccdcbc1d7c80018913c8f8ecb52e54e47870ea8ece361f0ae6054142f62ea71e6355cd16e7d3a5107362e3c9089d9dbcaf41e85399e815d748dc20017828622332f79bbc35bd1672229d4a2ec200458c8de8ff1c049a8c0e30b0d897b7040b58679d82db4db759f499ee7813e074b59e07974ca9dfbf450ed74a4cf259bd1e4ac44fc2cca3ab99bb7a3a51d1bc773fb6f29c60ddaed9f15a6ef868a028f60a65d877e2fa2cb9db920b9474e310f9808ec1e227cdc8775d82b4b39a561eb3f0ef443d55bb976abe7855c7a67c2c1997c31993674f733c997003bd59c9c0a523fb600232776af8d7c6990a8aab62306821"

        //2.create RequestBody
        val fileBody = RequestBody.create(MEDIA_TYPE_PNG, file!!)

        //3.build MultipartBody
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "receipt_image", fileBody)
            // .addFormDataPart("userName", userName.toString())
            .addFormDataPart("merchant", merchant.toString())
            .addFormDataPart("scan_date", tx1.text.toString())
            //.addFormDataPart("receipt_image", file!!
            .addFormDataPart("location", tx3.text.toString())
            .addFormDataPart("total", tx4.text.toString())
            .addFormDataPart("tax", tx5.text.toString())
            .addFormDataPart("tip", tx6.text.toString())
            .addFormDataPart("coupon", tx7.text.toString())
            .addFormDataPart("currency", tvCurrency.text.toString())
            .addFormDataPart("receipt_text", txt1.text.toString())
            .build()

        //4.build the request
        val request: Request = Request.Builder()
            .url("http://192.168.2.15:8000/api/receipts/")
           // .post(requestBody)
            .method("POST", requestBody)
            .addHeader("Content-type","text/plain;charset=UTF-8")
           // .addHeader("token", token)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(this)}")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            Log.i("Successful", "Receipt Saved to Database!")

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
                var months=(month + 1).toString()
                var dayOfMonths=dayOfMonth.toString()
                if (month<9){
                    months="0"+months
                }
                if (dayOfMonth<10){
                    dayOfMonths="0"+dayOfMonth
                }
                val text = year.toString() + "-" + months + "-" + dayOfMonths
                textView.text = text
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        dialog.show()
    }
}