package com.codenode.budgetlens.login.password_reset

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.databinding.ActivityPasswordResetBinding
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var emailAddress: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        val sendEmailButton: TextView = findViewById(R.id.sendEmail)
        val intent = Intent(this, CodeConfirmationActivity::class.java)

        emailAddress = findViewById(R.id.emailInput)

        //This will redirect the user to the code confirmation page
        sendEmailButton.setOnClickListener {
            //Verify the input is entered
            if (emailAddress.length() == 0) {
                emailAddress.error = "This field is required"
            }

            //create generate digit code URL
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/generateDigitCodeEndpoint/"

            val generateDigitCodePost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            //create and send request
            val body = ("{\r\n" +
                    "    \"email\": \"${emailAddress.text.toString()}\"\r\n" +
                    "}").trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()

            generateDigitCodePost.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                intent.putExtra("email", emailAddress.text.toString())


                                Log.i("Successful", "Generate successful.")
                                startActivity(intent)
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


}