package com.codenode.budgetlens.common

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
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
import com.codenode.budgetlens.R
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class ScanningReceiptActivity : AppCompatActivity() {

    private lateinit var getImage: Button
    private lateinit var imageView: ImageView
    private lateinit var receiptImage: File
    private var imagePath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning_receipt)

//        ActivityCompat.requestPermissions(this, arrayOf<String>(
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE),
//            PackageManager.PERMISSION_GRANTED)

        getImage = findViewById(R.id.getImage)
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
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            val pic = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(pic)
            storePicture(pic)
            confirm()
        }
    }

    private fun confirm() {

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
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}