package com.codenode.budgetlens.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.home.HomePageActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime

class ScanningReceiptActivity : AppCompatActivity() {

    private lateinit var getImage2: Button
    private lateinit var getGalleryImg: FloatingActionButton
    private lateinit var confirmImage: Button
    private lateinit var imageView: ImageView

    private var currentImgPath: String = ""
    private var tempFile: String? = ""

    private lateinit var imageUriGallery: Uri
    private lateinit var imageFileGallery: File


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning_receipt)
        CommonComponents.handleNavigationBar(ActivityName.SCAN, this, this.window.decorView)
        CommonComponents.handleScanningReceipts(this.window.decorView, this, ActivityName.SCAN)

        val context = this as Context
        val goToHomePageActivity = Intent(this, HomePageActivity::class.java)
        getImage2 = findViewById(R.id.getImage)
        getGalleryImg = findViewById(R.id.addFromGallery)
        confirmImage = findViewById(R.id.confirmReceipt)
        imageView = findViewById(R.id.imageView)

        getGalleryImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            tempFile = "Receipt_${LocalDateTime.now()}"
            var storageDirectory: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            try {
                var imgFile: File = File.createTempFile(tempFile,".png",storageDirectory)
                imageFileGallery = imgFile
                currentImgPath = imgFile.absolutePath // path to where images are saved:/storage/emulated/0/Android/data/com.codenode.budgetlens/files/Pictures
                imageUriGallery = FileProvider.getUriForFile(this, "com.codenode.budgetlens",imgFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriGallery)
                tempFile = currentImgPath.toString().split("/").last()
                startActivityForResult(intent, 2)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        getImage2.setOnClickListener{
            tempFile = "Receipt_${LocalDateTime.now()}"
            var storageDirectory: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            try {
                var imgFile: File = File.createTempFile(tempFile,".png",storageDirectory)
                currentImgPath = imgFile.absolutePath // path to where images are saved:/storage/emulated/0/Android/data/com.codenode.budgetlens/files/Pictures
                var imageUri: Uri = FileProvider.getUriForFile(this, "com.codenode.budgetlens",imgFile)
                var intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                tempFile = currentImgPath.toString().split("/").last()
                startActivityForResult(intent, 1)

            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        confirmImage.setOnClickListener {
            confirm(context, goToHomePageActivity)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            var bitmap: Bitmap = BitmapFactory.decodeFile(currentImgPath)
            var imageView: ImageView = findViewById(R.id.imageView)
            imageView.setImageBitmap(bitmap)
            getImage2.text = "Scan Again?"
            confirmImage.isEnabled = true
            confirmImage.alpha = 1.0F
        }
        if (requestCode == 2) {
            imageView.setImageURI(data?.data)
            this.contentResolver.openInputStream(data?.data!!)?.let { inputStream ->
                val fileOutputStream = FileOutputStream(imageFileGallery)
                inputStream.copyTo(fileOutputStream)
                inputStream.close()
                fileOutputStream.close()
            }
            confirmImage.isEnabled = true
            confirmImage.alpha = 1.0F
        }
    }

    private fun confirm(context: Context, goToHomePageActivity: Intent) {

        //TODO: UPDATE THE API FOR SAVING PICTURE AS A RECEIPT AS SOON AS AMIR PUSHES PR

        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/"
        val registrationPost = OkHttpClient()
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("receipt_image", tempFile,
                File(currentImgPath).asRequestBody("image/png".toMediaType()))
            .addFormDataPart("merchant","1")
            .addFormDataPart("location","1")
            .addFormDataPart("total","1")
            .addFormDataPart("tax","1")
            .addFormDataPart("tip","1")
            .addFormDataPart("coupon","1")
            .addFormDataPart("currency","USD")
            .addFormDataPart("important_dates","1999-01-01")
            .build()
        val request = Request.Builder()
            .url(url)
            .method("POST", requestBody)
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