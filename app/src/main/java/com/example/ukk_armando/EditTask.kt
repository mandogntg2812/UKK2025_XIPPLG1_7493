package com.example.ukk_armando

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private var taskId: Int = 0
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_task)

        dbHelper = DatabaseHelper(this)

        taskId = intent.getIntExtra("TASK_ID", 0)

        val task = dbHelper.getTaskById(taskId)

        // Cek jika task null
        if (task == null) {
            Toast.makeText(this, "Task tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish() // Jika task tidak ditemukan, kembali ke activity sebelumnya
            return
        }

        // Inisialisasi views
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateEditText = findViewById(R.id.dateEditText)
        timeEditText = findViewById(R.id.timeEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.saveButton)

        // Set data task yang ada
        descriptionEditText.setText(task.description)
        dateEditText.setText(task.date)
        timeEditText.setText(task.time)

        // Kategori
        val categories = arrayOf("Pekerjaan", "Pribadi", "Olahraga", "Belajar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Set category sesuai dengan task yang sedang diedit
        val categoryIndex = categories.indexOf(task.category)
        if (categoryIndex >= 0) {
            categorySpinner.setSelection(categoryIndex)
        }

        calendar = Calendar.getInstance()

        // Set listener untuk memilih tanggal
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Set listener untuk memilih waktu
        timeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        // Event untuk menyimpan perubahan
        saveButton.setOnClickListener {
            val description = descriptionEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()

            if (description.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Membuat task baru dengan data yang sudah diedit
            val updatedTask = Task(
                id = taskId, // Pastikan ID tetap sama
                description = description,
                date = date,
                time = time,
                category = category,
                isCompleted = task.isCompleted,
                isHistory = task.isHistory
            )

            // Memperbarui task di database
            dbHelper.updateTask(updatedTask)

            Toast.makeText(this, "Tugas berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke activity sebelumnya setelah update
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateEditText.setText(dateFormat.format(calendar.time))
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Membuka TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->

                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeEditText.setText(timeFormat.format(calendar.time))
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
}
