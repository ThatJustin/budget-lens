package com.codenode.budgetlens.login.password_reset

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.databinding.ActivityCodeConfimationBinding
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CodeConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCodeConfimationBinding
    private lateinit var confirmCode: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCodeConfimationBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_code_confimation)

        val confirmButton: TextView = findViewById(R.id.confirm)
        val intent = Intent(this, NewPasswordActivity::class.java)
        confirmCode = findViewById(R.id.codeInput)

        //This will redirect the user to the code confirmation page
        confirmButton.setOnClickListener {

            //get user email from the previous page
            val email = getIntent().getStringExtra("email")

            //Verify the input is entered
            if (confirmCode.length() == 0) {
                confirmCode.error = "This field is required"
            }

            //create validate digit code endpoint URL
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/validateDigitCodeEndpoint/"

            val validateCodePost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            //create and send request
            val body = ("{\r\n" +
                    "    \"email\": \"${email}\",\r\n" +
                    "    \"digit\": ${confirmCode.text}\r\n" +
                    "}").trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()

            validateCodePost.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                intent.putExtra("email", email)
                                Log.i("Successful", "Code match successful.")
                                startActivity(intent)
                            } else {
                                Log.i("Empty", "Something went wrong${response.body?.string()}")
                            }

                        } else {
                            runOnUiThread {
                                confirmCode.error = "Please enter the correct confirmation code."
                            }

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
}