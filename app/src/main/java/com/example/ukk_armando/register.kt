package com.example.ukk_armando

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(name, email, password)) {
                // Register logic here
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        // Check if fields are not empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
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
