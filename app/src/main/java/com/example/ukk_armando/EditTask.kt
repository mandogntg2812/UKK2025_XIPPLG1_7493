package com.example.ukk_armando

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var task: Task
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_task)

        dbHelper = DatabaseHelper(this)

        // Menghubungkan komponen UI dengan variabel
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateEditText = findViewById(R.id.dateEditText)
        timeEditText = findViewById(R.id.timeEditText)
        categoryEditText = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.saveButton)

        // Mengambil ID tugas dari Intent
        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            task = dbHelper.getTaskById(taskId) ?: run {
                Toast.makeText(this, "Tugas tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            // Menampilkan data tugas yang ada ke dalam EditText
            descriptionEditText.setText(task.description)
            dateEditText.setText(task.date)
            timeEditText.setText(task.time)
            categoryEditText.setText(task.category)
        }

        // Menangani klik tombol simpan
        saveButton.setOnClickListener {
            val updatedTask = Task(
                id = task.id,
                description = descriptionEditText.text.toString(),
                date = dateEditText.text.toString(),
                time = timeEditText.text.toString(),
                category = categoryEditText.text.toString(),
                isCompleted = task.isCompleted,
                isHistory = task.isHistory
            )

            val rowsAffected = dbHelper.updateTask(updatedTask)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Tugas berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()  // Kembali ke Activity sebelumnya setelah menyimpan perubahan
            } else {
                Toast.makeText(this, "Gagal memperbarui tugas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
