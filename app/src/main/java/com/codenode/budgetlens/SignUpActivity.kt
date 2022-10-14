package com.codenode.budgetlens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class SignUpActivity : AppCompatActivity() {
    //    5 text fields
    private lateinit var emailField: TextInputEditText
    private lateinit var firstNameField: TextInputEditText
    private lateinit var lastNameField: TextInputEditText
    private lateinit var telephoneField: TextInputEditText
    private lateinit var confirmPasswordField: TextInputEditText
    private lateinit var passwordField: TextInputEditText

    //    1 button
    private lateinit var registerButton: Button

    //    Check if fields have been met
    private var hasCorrectFields: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val goToWelcomePage = Intent(this, WelcomeActivity::class.java)

        val actionBar = supportActionBar

        actionBar!!.title = "Sign Up"

        actionBar.setDisplayHomeAsUpEnabled(true)

//        Initialize Text Fields
        emailField = findViewById(R.id.email)
        firstNameField = findViewById(R.id.firstName)
        lastNameField = findViewById(R.id.lastName)
        telephoneField = findViewById(R.id.telephoneNumber)
        confirmPasswordField = findViewById(R.id.confirmPassword)
        passwordField = findViewById(R.id.password)

//        Initialize Buttons
        registerButton = findViewById(R.id.filledButton)

//        On button click
        registerButton.setOnClickListener {
            checkFrontendFields()

            // Register using the rest API once all the frontend field checks passed
            if (hasCorrectFields) {
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
                        "    \"telephone_number\": \"${findViewById<TextView>(R.id.telephoneNumber).text}\"\r\n" +
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
                                    "Something went wrong${response.body?.string().toString()} ${response.message} ${response.headers}"
                                )
                            } else {
                                Log.i("Successful", "${response.body?.string()}")
                                startActivity(goToWelcomePage)
                            }
                        }

                    }
                })
            }
        }
    }

    private fun checkFrontendFields() {
        if (emailField.length() == 0) {
            emailField.error = "This field is required"
            hasCorrectFields = false
        }

        if (firstNameField.length() == 0) {
            firstNameField.error = "This field is required"
            hasCorrectFields = false
        }

        if (lastNameField.length() == 0) {
            lastNameField.error = "This field is required"
            hasCorrectFields = false
        }

        if (telephoneField.length() == 0) {
            telephoneField.error = "This field is required"
            hasCorrectFields = false
        }

        if (passwordField.length() == 0) {
            passwordField.error = "This field is required"
            hasCorrectFields = false
        }

        if (confirmPasswordField.length() == 0) {
            confirmPasswordField.error = "This field is required"
            hasCorrectFields = false
            return
        }

        if (confirmPasswordField.text.toString() != passwordField.text.toString()) {
            Log.d(
                "Debug Password",
                "passwords don't match:${confirmPasswordField.text.toString()}!=${passwordField.text.toString()}"
            )
            confirmPasswordField.error = "Passwords do not match"
            hasCorrectFields = false
            return
        }

        if (passwordField.text.toString().length < 8) {
            passwordField.error = "Password must be greater than 8 character"
            hasCorrectFields = false
            return
        }

        hasCorrectFields = true
    }
}