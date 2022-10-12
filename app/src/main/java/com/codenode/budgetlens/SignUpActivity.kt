package com.codenode.budgetlens

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val actionBar = supportActionBar

        actionBar!!.title = "Sign Up"

        actionBar.setDisplayHomeAsUpEnabled(true)

        val registerButton: Button = findViewById(R.id.filledButton)

        registerButton.setOnClickListener {
            val url = "http://${BuildConfig.IP_ADDRESS}:${BuildConfig.PORT_NUMBER}/registerEndpoint/"

            val registrationPost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            val body = ("{\r\n" +
                    "    \"user\": {\r\n" +
                    "        \"username\": \"${findViewById<TextView>(R.id.email).text}\",\r\n" +
                    "        \"first_name\": \"${findViewById<TextView>(R.id.firstName).text}\",\r\n" +
                    "        \"last_name\": \"${findViewById<TextView>(R.id.lastName).text}\",\r\n" +
                    "        \"email\": \"${findViewById<TextView>(R.id.email).text}\",\r\n" +
                    "        \"password\": \"${findViewById<TextView>(R.id.password).text}\"\r\n" +
                    "    },\r\n" +
                    "    \"telephone_number\": 5143572412\r\n" +
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
                        if (!response.isSuccessful) {
                            Log.e(
                                "Error",
                                "Something went wrong${response.body?.string()} ${response.message} ${response.headers}"
                            )
                        } else {
                            Log.i("Successful", "${response.body?.string()}")
                        }
                    }

                }
            })
        }
    }
}