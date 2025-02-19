package com.example.ukk_armando

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private var taskId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        dbHelper = DatabaseHelper(this)

        // Mendapatkan ID tugas dari Intent
        taskId = intent.getIntExtra("TASK_ID", 0)

        // Mengambil data tugas berdasarkan ID
        val task = dbHelper.getTaskById(taskId)

        // Inisialisasi komponen UI
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateEditText = findViewById(R.id.dateEditText)
        timeEditText = findViewById(R.id.timeEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.saveTaskButton)

        // Menampilkan data tugas yang sudah ada
        descriptionEditText.setText(task.description)
        dateEditText.setText(task.date)
        timeEditText.setText(task.time)

        // Menyiapkan kategori spinner
        val categories = arrayOf("Pekerjaan", "Pribadi", "Olahraga", "Belajar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Menyeting kategori yang sesuai
        categorySpinner.setSelection(categories.indexOf(task.category))

        // Menyimpan perubahan tugas yang diedit
        saveButton.setOnClickListener {
            val description = descriptionEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()

            // Validasi input
            if (description.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Membuat task yang diperbarui
            // Pastikan isCompleted adalah Boolean, bukan String
            val updatedTask = Task(
                taskId,
                description,
                date,
                time,
                category,
                isCompleted = task.isCompleted, // Menggunakan nilai yang ada pada task (boolean)
                isHistory = task.isHistory // Menggunakan nilai yang ada pada task (boolean)
            )

            // Memperbarui tugas di database
            dbHelper.updateTask(updatedTask)

            // Menampilkan pesan sukses
            Toast.makeText(this, "Tugas berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            finish()  // Menutup activity setelah tugas diperbarui
        }
    }
}
