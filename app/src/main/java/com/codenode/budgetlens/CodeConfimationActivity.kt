package com.codenode.budgetlens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.codenode.budgetlens.databinding.ActivityCodeConfimationBinding
import com.codenode.budgetlens.databinding.ActivityPasswordResetBinding

class CodeConfimationActivity : AppCompatActivity() {
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