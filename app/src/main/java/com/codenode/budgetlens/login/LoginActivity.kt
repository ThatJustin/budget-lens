package com.codenode.budgetlens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.util.Log
import android.widget.Button
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.HomePageActivity
import com.codenode.budgetlens.R
import com.codenode.budgetlens.SignUpActivity
import com.codenode.budgetlens.googlelogin.GoogleLoginSecondActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    var mGoogleSignInClient: GoogleSignInClient? = null
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

        val registerButton: Button = findViewById(R.id.createNewUser)

        //This will redirect the user to the register page
        registerButton.setOnClickListener(){
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Will eventually need to block login when user is already logged in
        loginButton.setOnClickListener {

            if(usernameField.length() == 0){
                usernameField.error = "This field is required"
            }
            if(passwordField.length() == 0){
                passwordField.error = "This field is required"
            }

            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/loginEndpoint/"

            val registrationPost = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            val body = ("{\r\n" +
                    "    \"username\": \"${usernameField.text}\",\r\n" +
                    "    \"password\": \"${passwordField.text}\"\r\n" +
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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            navigateToSecondActivity()
        }
        val signInButton = findViewById<ImageView>(R.id.google_sign_in)
        signInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

         if (requestCode == RC_SIGN_IN) {
             val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.getResult(ApiException::class.java)
                navigateToSecondActivity()
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun navigateToSecondActivity() {
        finish()
        val intent = Intent(this@LoginActivity, GoogleLoginSecondActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }

}