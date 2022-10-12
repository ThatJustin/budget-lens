package com.codenode.budgetlens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val actionBar = supportActionBar

        actionBar!!.title = "Login"

        actionBar.setDisplayHomeAsUpEnabled(true)

        val loginButton: Button = findViewById(R.id.checkCredentials)

        loginButton.setOnClickListener {
            val url = "http://192.168.2.56:8000/registerEndpoint/"

            val registrationPost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            val body = ("{\r\n" +
                    "    \"username\": \"teo14\",\r\n" +
                    "    \"password\": \"1234palo\"\r\n" +
                    "}").trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()

            registrationPost.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            Log.i("Successful", "${response.body?.string()}")
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