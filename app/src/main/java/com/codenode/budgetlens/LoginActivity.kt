package com.codenode.budgetlens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.codenode.budgetlens.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_login)


        val actionBar = supportActionBar

        actionBar!!.title = "Login"

        actionBar.setDisplayHomeAsUpEnabled(true)

        val registerButton: Button = findViewById(R.id.createNewUser)

        registerButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

//        binding.createNewUser.setOnClickListener {
//            val intent = Intent(this, SignUpActivity::class.java)
//            startActivity(intent)
//        }
    }
}