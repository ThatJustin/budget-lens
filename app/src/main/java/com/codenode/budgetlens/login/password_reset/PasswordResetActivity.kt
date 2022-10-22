package com.codenode.budgetlens.login.password_reset

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.codenode.budgetlens.R
import com.codenode.budgetlens.databinding.ActivityPasswordResetBinding

class PasswordResetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordResetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.sendEmail.setOnClickListener {
            if (isEmailValid(binding.emailInput.text.toString())) {
                Toast.makeText(this, getString(R.string.email_sent), Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CodeConfirmationActivity::class.java))
            } else {
                Toast.makeText(this, getString(R.string.invalide_email_address), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun isEmailValid(emailAddress: String): Boolean {
        return emailAddress.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()
    }
}