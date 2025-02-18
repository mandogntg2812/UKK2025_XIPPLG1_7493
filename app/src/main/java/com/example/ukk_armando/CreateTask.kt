package com.example.ukk_armando

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CreateTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val description = findViewById<EditText>(R.id.etDescription).text.toString()
            val day = findViewById<EditText>(R.id.etDay).text.toString()
            val time = findViewById<EditText>(R.id.etTime).text.toString()

            saveTask(description, day, time)
        }
    }

    private fun saveTask(description: String, day: String, time: String) {
        // Save task to SQLite database
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("description", description)
            put("day", day)
            put("time", time)
        }
        db.insert("tasks", null, values)
    }
}