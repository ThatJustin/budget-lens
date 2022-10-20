package com.codenode.budgetlens.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.home.HomePageActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.i18n.phonenumbers.PhoneNumberUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class SignUpActivity : AppCompatActivity() {
    // 5 text fields
    private lateinit var emailField: TextInputEditText
    private lateinit var firstNameField: TextInputEditText
    private lateinit var lastNameField: TextInputEditText
    private lateinit var telephoneField: TextInputEditText
    private lateinit var confirmPasswordField: TextInputEditText
    private lateinit var passwordField: TextInputEditText

    // 1 button
    private lateinit var registerButton: Button

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val goToWelcomePage = Intent(this, HomePageActivity::class.java)

        // Initialize Text Fields
        emailField = findViewById(R.id.email)
        firstNameField = findViewById(R.id.firstName)
        lastNameField = findViewById(R.id.lastName)
        telephoneField = findViewById(R.id.telephoneNumber)
        confirmPasswordField = findViewById(R.id.confirmPassword)
        passwordField = findViewById(R.id.password)

        // Initialize Buttons
        registerButton = findViewById(R.id.filledButton)

        // On button click
        registerButton.setOnClickListener {
            hasValidFieldsFrontend()

            // Register using the rest API once all the frontend field checks passed
            if (hasValidFieldsFrontend()) {
                val url =
                    "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/registerEndpoint/"

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
                                val jsonDataString = response.body?.string().toString()
                                Log.e(
                                    "Error",
                                    "Something went wrong${jsonDataString} ${response.message} ${response.headers}"
                                )

                                hasValidFieldsBackend(jsonDataString)
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

    private fun hasValidFieldsBackend(jsonDataString: String) {
        this@SignUpActivity.runOnUiThread {
            val json = JSONObject(jsonDataString)

            if (json.has("user")) {
                if (json.getJSONObject("user").has("email")) {
                    var email = json.getJSONObject("user").getJSONArray("email").toString()
                    email = email.substringAfter("\"").substringBefore("\"")
                    emailField.error = email
                }

                if (json.getJSONObject("user").has("first_name")) {
                    var firstName = json.getJSONObject("user").getJSONArray("first_name").toString()
                    firstName = firstName.substringAfter("\"").substringBefore("\"")
                    firstNameField.error = firstName

                }

                if (json.getJSONObject("user").has("last_name")) {
                    var lastName = json.getJSONObject("user").getJSONArray("last_name").toString()
                    lastName = lastName.substringAfter("\"").substringBefore("\"")
                    lastNameField.error = lastName

                }

                if (json.getJSONObject("user").has("password")) {
                    var password = json.getJSONObject("user").getJSONArray("password").toString()
                    password = password.substringAfter("\"").substringBefore("\"")
                    passwordField.error = password

                }
            }
            if (json.has("telephone_number")) {
                var telephoneNumber = json.getJSONArray("telephone_number").toString()
                telephoneNumber = telephoneNumber.substringAfter("\"").substringBefore("\"")
                telephoneField.error = telephoneNumber

            }
        }

    }

    private fun hasValidFieldsFrontend(): Boolean {
        if (emailField.length() == 0) {
            emailField.error = "This field is required"
        }

        if (firstNameField.length() == 0) {
            firstNameField.error = "This field is required"
        }

        if (lastNameField.length() == 0) {
            lastNameField.error = "This field is required"
        }

        if (telephoneField.length() == 0) {
            telephoneField.error = "This field is required"
        }

        if (passwordField.length() == 0) {
            passwordField.error = "This field is required"
        }

        if (confirmPasswordField.length() == 0) {
            confirmPasswordField.error = "This field is required"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailField.text.toString()).matches()) {
            emailField.error = "This field is not a valid email address"
            return false
        }

        val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
        try {
            val phoneNumber = phoneUtil.parse(telephoneField.text.toString(), "CA")
            if (!phoneUtil.isValidNumber(phoneNumber)) {
                telephoneField.error = "This field is not a valid telephone number"
                return false
            }
        } catch (e: Exception) {
            telephoneField.error = "This field is not a valid telephone number"
            return false
        }

        if (confirmPasswordField.text.toString() != passwordField.text.toString()) {
            Log.d(
                "Debug Password",
                "passwords don't match:${confirmPasswordField.text.toString()}!=${passwordField.text.toString()}"
            )
            confirmPasswordField.error = "Passwords do not match"
            return false
        }

        if (passwordField.text.toString().length < 8) {
            passwordField.error = "Password must be greater than 8 character"
            return false
        }

        return true
    }
}