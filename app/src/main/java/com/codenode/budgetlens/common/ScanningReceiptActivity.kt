package com.codenode.budgetlens.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.UserProfile
import com.codenode.budgetlens.home.HomePageActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime

class ScanningReceiptActivity : AppCompatActivity() {

    private lateinit var getImage: Button
    private lateinit var confirmImage: Button
    private lateinit var imageView: ImageView
    private var imagePath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning_receipt)

        val context = this as Context
        val goToHomePageActivity = Intent(this, HomePageActivity::class.java)
        getImage = findViewById(R.id.getImage)
        confirmImage = findViewById(R.id.confirmReceipt)
        imageView = findViewById(R.id.imageView)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                101
            )
        }

        getImage.setOnClickListener {
            var img = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(img, 101)
        }
        confirmImage.setOnClickListener {
            confirm(context, goToHomePageActivity)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            val pic = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(pic)
            storePicture(pic)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImage.isEnabled = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun storePicture(receiptPhoto: Bitmap) {
        val root = Environment.getDataDirectory().toString()
        val fileName = "Receipt_${LocalDateTime.now()}.jpg".replace(":", ".")

        val file = File(this.cacheDir, fileName)

        Log.i("Test", file.toString())

//        val file = File("$root/Download/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        }
        Log.i("path", file.absolutePath)
        try {
            val output = FileOutputStream(file)
            receiptPhoto.compress(Bitmap.CompressFormat.JPEG, 100, output)
            imagePath = file.absolutePath
            output.flush()
            output.close()
            confirmImage.isEnabled = true
            confirmImage.alpha = 1.0F
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun confirm(context: Context, goToHomePageActivity: Intent) {

        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/"

        val registrationPost = OkHttpClient()

        val mediaType = "multipart/form-data".toMediaTypeOrNull()

        val body = ("{\r\n" +
                "    \"receipt_image\": \"${imagePath}\",\r\n" +
                "}").trimIndent().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .method("POST", body)
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
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
                            val jsonObject = JSONObject(responseBody.toString())
                            //Parse the bearer token from response
                            val token = jsonObject.getString("token")

                            //Save token and load profile (if it already exists, it just updates )
                            BearerToken.saveToken(token, context)
                            UserProfile.loadProfileFromAPI(context)

                            Log.i("Successful", "Receipt Saved to Database!")
                            startActivity(goToHomePageActivity)
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