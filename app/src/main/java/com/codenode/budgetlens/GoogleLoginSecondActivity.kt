package com.codenode.budgetlens

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import android.widget.TextView
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.content.Intent
import android.view.View
import android.widget.Button


class GoogleLoginSecondActivity : AppCompatActivity() {
    private lateinit var email:TextView
    private lateinit var name:TextView
    private lateinit var signOutBtn:Button
    private var gso: GoogleSignInOptions? = null
    private var gsc: GoogleSignInClient? = null
    //var name: TextView? = null
    //var email: TextView? = null
    //var signOutBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login_second)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        signOutBtn = findViewById(R.id.signout)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso!!)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val personName = account.displayName
            val personEmail = account.email
            name.setText(personName)
            email.setText(personEmail)
        }
        signOutBtn.setOnClickListener(View.OnClickListener { signOut() })
    }

    private fun signOut() {
        gsc!!.signOut().addOnCompleteListener {
            finish()
            startActivity(Intent(this@GoogleLoginSecondActivity, GoogleLoginActivity::class.java))
        }
    }
}