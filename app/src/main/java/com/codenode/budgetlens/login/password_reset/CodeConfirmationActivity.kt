package com.codenode.budgetlens.login.password_reset

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codenode.budgetlens.R
import com.codenode.budgetlens.databinding.ActivityCodeConfimationBinding

class CodeConfirmationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodeConfimationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeConfimationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.confirm.setOnClickListener {
            if (binding.codeInput.text.toString()
                    .isNotEmpty() && binding.codeInput.text.toString().length == 6
            ) {
                startActivity(Intent(this, NewPasswordActivity::class.java))
            } else {
                Toast.makeText(this, getString(R.string.code_message), Toast.LENGTH_LONG).show()
            }
        }
    }
}