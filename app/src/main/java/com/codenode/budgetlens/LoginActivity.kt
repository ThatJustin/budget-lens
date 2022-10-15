package com.codenode.budgetlens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameField: TextInputEditText

    private lateinit var passwordField: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val goToHomePageActivity = Intent(this, HomePageActivity::class.java)

        val loginButton: Button = findViewById(R.id.checkCredentials)

        val actionBar = supportActionBar

        usernameField = findViewById<TextInputEditText>(R.id.usernameText)

        passwordField = findViewById<TextInputEditText>(R.id.passwordText)

        actionBar!!.title = "Login"

        actionBar.setDisplayHomeAsUpEnabled(true)

        // Will eventually need to block login when user is already logged in
        loginButton.setOnClickListener {

            if(usernameField.length() == 0){
                usernameField.error = "This field is required"
            }
            if(passwordField.length() == 0){
                passwordField.error = "This field is required"
            }

                val url = "http://IP_ADDRESS_HERE:8000/loginEndpoint/"

                val registrationPost = OkHttpClient()

                val mediaType = "application/json".toMediaTypeOrNull()

                val body = ("{\r\n" +
                        "    \"username\": \"${findViewById<TextView>(R.id.usernameText).text}\",\r\n" +
                        "    \"password\": \"${findViewById<TextView>(R.id.passwordText).text}\"\r\n" +
                        "}").trimIndent().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                registrationPost.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.i("Response", "Got the response from server")
                        response.use {
                            if (response.isSuccessful) {
                                val body = response.body?.string()
                                if (body != null) {
                                    Log.i("Successful", body)
                                    startActivity(goToHomePageActivity)
                                }
                                else{
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
class HomePage(val token: String)