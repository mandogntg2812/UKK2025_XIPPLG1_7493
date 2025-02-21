package com.example.ukk_armando

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Mengambil referensi ke UI
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Mengambil data pengguna dari SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "User Name")
        val userEmail = sharedPreferences.getString("email", "user@example.com")

        // Menampilkan nama dan email pengguna
        tvName.text = "Name: $userName"
        tvEmail.text = "Email: $userEmail"

        // Tombol Logout
        btnLogout.setOnClickListener {
            // Menghapus data pengguna dari SharedPreferences (Logout)
            val editor = sharedPreferences.edit()
            editor.clear() // Menghapus semua data
            editor.apply()

            // Arahkan ke LoginActivity setelah logout
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Menutup ProfileActivity agar tidak kembali
        }
    }
}
