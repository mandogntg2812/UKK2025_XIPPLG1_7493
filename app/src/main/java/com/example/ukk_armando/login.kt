package com.example.ukk_armando

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                // Login logic here
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        // Check if email and password are not empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if email and password contain only lowercase letters
        if (!email.matches("^[a-z]+$".toRegex())) {
            Toast.makeText(this, "Email must only contain lowercase letters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.matches("^[a-z]+$".toRegex())) {
            Toast.makeText(this, "Password must only contain lowercase letters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
