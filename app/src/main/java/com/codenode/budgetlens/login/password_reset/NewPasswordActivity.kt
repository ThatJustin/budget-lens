package com.codenode.budgetlens.login.password_reset

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var newPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        val resetPasswordButton: TextView = findViewById(R.id.resetPassword)
        val intent = Intent(this, LoginActivity::class.java)

        newPassword = findViewById(R.id.newPasswordInput)
        confirmPassword = findViewById(R.id.confirmPasswordInput)

        //This will redirect the user to the code login page
        resetPasswordButton.setOnClickListener {

            //get user email from the previous page
            val email = getIntent().getStringExtra("email")

            //Verify the input is entered
            if (newPassword.length() == 0) {
                newPassword.error = "This field is required"
            }
            if (confirmPassword.length() == 0) {
                confirmPassword.error = "This field is required"
            }

            //create change password endpoint URL
            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/changePasswordEndpoint/"

            val validateCodePost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            //create and send request
            val body = ("{\r\n" +
                    "    \"email\": \"${email}\",\r\n" +
                    "    \"new_password\": \"${newPassword.text}\",\r\n" +
                    "    \"re_password\": \"${confirmPassword.text}\"\r\n" +
                    "}").trimIndent().toRequestBody(mediaType)
            Log.i("test", body.toString())
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
                                intent.putExtra("passwordChangeSuccess", true)
                                Log.i("Successful", "Password changed successful.")
                                startActivity(intent)
                            } else {
                                Log.i("Empty", "Something went wrong${response.body?.string()}")
                            }
                        } else {
                            runOnUiThread {
                                confirmPassword.error = "The entered password doesn't match."
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